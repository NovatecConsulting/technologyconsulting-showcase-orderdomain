package de.novatec.showcase.order.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Properties;


public class KafkaProducerCreator {

    @Produces
    @ApplicationScoped
    public Producer<Integer, JsonNode> createProducer(){
        String broker = System.getenv("KAFKA_BROKER");
        Properties props = new Properties();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,broker!=null?broker:KafkaConfiguration.KAFKA_BROKERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, KafkaConfiguration.PRODUCER_CLIENT_ID);
        props.put(ProducerConfig.ACKS_CONFIG,KafkaConfiguration.ACKS_CONFIG);

        return new KafkaProducer<Integer, JsonNode>(props);
    }
}
