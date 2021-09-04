# Kafka Avro Producer and Consumer

This is a tiny project using Java and Kafka, to produce and consume data in Avro format.

## Up and Running

```bash
docker-compose up -d zookeeper kafka schema-registry
docker-compose logs -f --tail 50

docker-compose up -d --build producer-avro
docker-compose logs -f --tail 50 producer-avro

docker-compose up -d --build consumer-avro
docker-compose logs -f --tail 50 consumer-avro

# consume with schema-registry
docker-compose exec schema-registry sh -c \
"kafka-avro-console-consumer \
  --bootstrap-server kafka:9092 \
  --topic \"customer-avro\" \
  --from-beginning \
  --property schema.registry.url=http://localhost:8081"
```
