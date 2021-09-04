package com.github.juliocesarscheidt.javaavro;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import io.confluent.kafka.serializers.KafkaAvroSerializer;

public class ProducerAvroApplication {

  public static KafkaProducer<String, Customer> createProducer(String bootstrapServers, String schemaRegistry) {
    Properties config = new Properties();

    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    config.put("schema.registry.url", schemaRegistry);

    // using string serializer
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    // using avro serializer
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());

    config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    config.put(ProducerConfig.ACKS_CONFIG, "all");
    config.put(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
    config.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 100);
    config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
    config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432); // 32 MB
    config.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 33554432); // 32 MB
    config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy"); // less compression than gzip, but faster
    config.put(ProducerConfig.BATCH_SIZE_CONFIG, 32768); // 32 KB

    config.put(ProducerConfig.LINGER_MS_CONFIG, 5); // 5 ms
    config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30 * 1000); // 30 * 1000 ms
    // delivery.timeout.ms should be equal to or larger than linger.ms + request.timeout.ms
    config.put("delivery.timeout.ms", 5 + (30 * 1000)); // 30005 ms

    return new KafkaProducer<String, Customer>(config);
  }

  public static void sendMessage(KafkaProducer<String, Customer> producer, String topic, Customer customer) {
    // send a message to topic, asynchronously
    ProducerRecord<String, Customer> record = new ProducerRecord<String, Customer>(topic, customer);

    producer.send(record, new Callback() {
      @Override
      public void onCompletion(RecordMetadata metadata, Exception exception) {
        if (exception != null) {
          System.out.println("Send failed for record " + exception);
        } else {
          System.out.println("[INFO] metadata offset " + metadata.offset());
          System.out.println("[INFO] metadata partition " + metadata.partition());
          System.out.println("[INFO] metadata topic " + metadata.topic());
          System.out.println("[INFO] metadata timestamp " + metadata.timestamp());
        }
      }
    });
  }

  public static void main(String[] args) {
    System.out.println("Starting Avro Producer");

    String bootstrapServers = System.getenv("BOOTSTRAP_SERVERS") != null ?
      System.getenv("BOOTSTRAP_SERVERS") : "kafka:9092";

    String schemaRegistry = System.getenv("SCHEMA_REGISTRY_URL") != null ?
      System.getenv("SCHEMA_REGISTRY_URL") : "http://schema-registry:8081";

    String topic = System.getenv("TOPIC_NAME") != null ?
      System.getenv("TOPIC_NAME") : "customer-avro";

    KafkaProducer<String, Customer> producer = createProducer(bootstrapServers, schemaRegistry);

    for (int i = 0; i <= 10; i ++) {
      Customer customer = Customer.newBuilder()
        .setFirstName("Julio " + String.valueOf(i))
        .setLastName("Cesar " + String.valueOf(i))
        .setAge(20 + i)
        .setHeight(180.0f)
        .setWeight(80.0f)
        .setAutomatedEmail(i % 2 == 0 ? false : true)
        .build();

      sendMessage(producer, topic, customer);
      System.out.println("Message sent " + customer.toString());
    }

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("Flushing and Closing Producer");
      producer.flush();
      producer.close();
    }));
  }
}
