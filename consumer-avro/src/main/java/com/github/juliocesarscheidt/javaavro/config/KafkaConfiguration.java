package com.github.juliocesarscheidt.javaavro.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;

@EnableKafka
@Configuration
public class KafkaConfiguration {

  public Map<String, Object> getConfig() {
    // create the config
    Map<String, Object> config = new HashMap<>();

    String bootstrapServers = System.getenv("BOOTSTRAP_SERVERS") != null ?
      System.getenv("BOOTSTRAP_SERVERS") : "kafka:9092";

    String schemaRegistry = System.getenv("SCHEMA_REGISTRY_URL") != null ?
      System.getenv("SCHEMA_REGISTRY_URL") : "http://schema-registry:8081";

    config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    config.put("schema.registry.url", schemaRegistry);

    // to receive strings we need a string deserializer
    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());

    // the consumer group id
    config.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-avro-group");

    // earliest, latest, none
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    // auto commit
    config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

    return config;
  }

  @Bean
  public ConsumerFactory<String, GenericRecord> avroConsumerFactory() {
    return new DefaultKafkaConsumerFactory<String, GenericRecord>(getConfig());
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, GenericRecord> avroListenerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, GenericRecord> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(avroConsumerFactory());
    return factory;
  }
}
