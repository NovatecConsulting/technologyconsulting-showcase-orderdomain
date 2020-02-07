package de.novatec.showcase.order.kafka;

public interface KafkaConfiguration {
    public static String KAFKA_BROKERS = "kafka:9092";
    public static String TOPIC_NAME="order";
    public static String PRODUCER_CLIENT_ID="producer1";
}
