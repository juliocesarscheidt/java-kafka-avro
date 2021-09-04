package com.github.juliocesarscheidt.javaavro.services;

import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.github.juliocesarscheidt.javaavro.model.Customer;

@Service
public class KafkaListenerService {

  @KafkaListener(topics = "customer-avro", groupId = "consumer-avro-group", containerFactory = "avroListenerFactory")
  public void consume(GenericRecord record) {
    System.out.println(record.getSchema());

    Customer customer = new Customer(
      record.get("first_name").toString(),
      record.get("last_name").toString(),
      Integer.parseInt(record.get("age").toString()),
      Float.parseFloat(record.get("height").toString()),
      Float.parseFloat(record.get("weight").toString()),
      Boolean.parseBoolean(record.get("automated_email").toString())
    );

    System.out.println(customer.toString());
  }
}
