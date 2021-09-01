package com.github.juliocesarscheidt.javaavro;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import io.confluent.kafka.serializers.KafkaAvroSerializer;

public class JavaAvroApplication {

  public static KafkaProducer<String, Customer> createProducer() {
    Properties config = new Properties();

    String bootstrapServers = System.getenv("BOOTSTRAP_SERVERS") != null ?
      System.getenv("BOOTSTRAP_SERVERS") : "127.0.0.1:9092";
    String schemaRegistry = System.getenv("SCHEMA_REGISTRY_URL") != null ?
      System.getenv("SCHEMA_REGISTRY_URL") : "http://127.0.0.1:8081";

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
    config.put("delivery.timeout.ms", 30 * 1000); // 30 secs
    config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
    config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432); // 32 MB
    config.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 33554432); // 32 MB
    config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy"); // less compression than gzip, but faster
    config.put(ProducerConfig.BATCH_SIZE_CONFIG, 32768); // 32 KB

    config.put(ProducerConfig.LINGER_MS_CONFIG, 5); // 5 ms
    config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30 * 1000); // 30 * 1000 ms
    // delivery.timeout.ms should be equal to or larger than linger.ms + request.timeout.ms
    config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 5 + (30 * 1000)); // 30005 ms

    return new KafkaProducer<String, Customer>(config);
  }

  public static void sendMessage(String topic, Customer customer) {
    KafkaProducer<String, Customer> producer = createProducer();

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

    producer.flush();
    producer.close();
  }

  public static void main(String[] args) {
    System.out.println("Starting");

    String topic = System.getenv("TOPIC_NAME") != null ?
    System.getenv("TOPIC_NAME") : "customer-avro";

    Customer customer = Customer.newBuilder()
      .setFirstName("Julio")
      .setLastName("Cesar")
      .setAge(25)
      .setHeight(180.0f)
      .setWeight(80.0f)
      .setAutomatedEmail(false)
      .build();

    sendMessage(topic, customer);
    System.out.println("Message sent");
  }
}
