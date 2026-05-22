# Docker assets

## Kafka (Step 8)

```bash
docker compose -f docker/docker-compose.kafka.yml up -d
# Or: ./scripts/start-kafka.sh
```

- Kafka broker: `localhost:9092`
- Kafka UI: http://localhost:8090
- Zookeeper: `localhost:2181`

## Full stack (Step 12+)

- Per-service `Dockerfile`
- Root `docker-compose.yml` for all services + databases
