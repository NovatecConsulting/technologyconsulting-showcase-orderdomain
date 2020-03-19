package de.novatec.showcase.order.kafka;

import de.novatec.showcase.order.dto.Order;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
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
    public Producer<Integer, Order> createProducer(){
        String broker = System.getenv("KAFKA_BROKER");
        String schemaRegistry = System.getenv("SCHEMA_REGISTRY");
        Properties props = new Properties();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,broker!=null?broker:KafkaConfiguration.KAFKA_BROKERS);
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,schemaRegistry!=null?schemaRegistry:KafkaConfiguration.SCHEMA_REGISTRY);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, KafkaConfiguration.PRODUCER_CLIENT_ID);
        props.put(ProducerConfig.ACKS_CONFIG,KafkaConfiguration.ACKS_CONFIG);
        //Timeout configuration of Kafka producer. Default configuration: send() method timeout after 2 minutes
//        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG,10000);
//        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG,5000);
//        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG,8000);
//        props.put(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG,10000);
//        props.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG,5000);

        return new KafkaProducer<Integer, Order>(props);
    }
}
