FROM openjdk:17-jdk-slim
LABEL service = kafka-stream-pipeline
COPY target/kafka-stream-pipeline-1.0-SNAPSHOT.jar /app.jar
EXPOSE 8080

ENV KAFKA_BOOTSTRAP_SERVERS=localhost:9092 \
    KAFKA_STREAMS_APP_ID=streaming-app \
    KAFKA_STREAMS_SERDE=org.apache.kafka.common.serialization.Serdes$StringSerde \
    KAFKA_SSL_TRUSTSTORE_LOCATION=/path/to/your/truststore.jks \
    KAFKA_SSL_TRUSTSTORE_PASSWORD=your-truststore-password \
    KAFKA_STREAMS_TOPICS=topic1,topic2,topic3 \
    DB_URL=jdbc:postgresql://localhost:5432/kafka \
    DB_USERNAME=kafka1 \
    DB_PASSWORD=kafka1 \
    DB_DRIVER_CLASS_NAME=org.postgresql.Driver \
    DB_POOL_SIZE=10 \
    SCHEDULED_FIXED_RATE=5000

ENTRYPOINT ["java", "-jar", "/app.jar"]