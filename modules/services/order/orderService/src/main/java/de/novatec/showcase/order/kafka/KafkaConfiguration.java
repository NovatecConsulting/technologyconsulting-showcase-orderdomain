package de.novatec.showcase.order.kafka;

public interface KafkaConfiguration {
    public static String KAFKA_BROKERS = "localhost:9092";
    public static String TOPIC_NAME="order";
    public static String PRODUCER_CLIENT_ID="producer1";
    public static String SCHEMA_REGISTRY="http://localhost:8081";

    public static String ACKS_CONFIG="all";
}
