# Kafka Avro Producer

## Up and Running

```bash
docker-compose up -d zookeeper kafka schema-registry
docker-compose logs -f --tail 50


docker-compose up -d --build producer-avro
docker-compose logs -f --tail 50 producer-avro


docker-compose exec schema-registry sh -c \
"kafka-avro-console-consumer \
  --bootstrap-server kafka:9092 \
  --topic \"customer-avro\" \
  --from-beginning \
  --property schema.registry.url=http://localhost:8081"
```
