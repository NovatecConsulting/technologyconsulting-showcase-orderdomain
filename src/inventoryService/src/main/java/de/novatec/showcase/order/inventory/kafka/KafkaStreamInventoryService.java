package de.novatec.showcase.order.inventory.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericEnumSymbol;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Properties;

@Configuration
public class KafkaStreamInventoryService {

    @Value("${kafka.broker}")
    private String bootstrapAddress;

    @Value("${kafka.stream.applicationid}")
    private String applicationid;

    @Value("${kafka.topic.order}")
    private String topic;

    @Value("${kafka.schemaregistry}")
    private String schemaRegistry;

    @Bean
    public KafkaStreams kafkaStreams(){
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationid);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry);
        final KafkaStreams kafkaStreams = new KafkaStreams(kafkaStreamTopology(), props);
        kafkaStreams.start();

        return kafkaStreams;
    }


    @Bean
    public Topology kafkaStreamTopology(){
        Serde<JsonNode> jsonSerde = Serdes.serdeFrom(new JsonSerializer(), new JsonDeserializer());
        final StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<Integer, GenericRecord> order = streamsBuilder.stream(topic);
        KStream<Integer, GenericRecord> updatedOrder = order.filter((key, value) -> notFulfilled(value))
                                                       .mapValues(value -> updateStatus(value));

        updatedOrder.to(topic);

        return streamsBuilder.build();
    }

    public void setTopic(String topic){
        this.topic = topic;
    }

    private boolean notFulfilled(GenericRecord record){
        GenericEnumSymbol status = (GenericEnumSymbol) record.get("status");
        if(!status.toString().equals("FULFILLED")) {
            return true;
        }
        return false;
    }

    private GenericRecord updateStatus(GenericRecord value){
        Schema schema = value.getSchema();
        value.put("status", new GenericData.EnumSymbol(schema.getField("status").schema(), "FULFILLED"));
        return value;
    }

}