package de.novatec.showcase.order.inventory.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

import java.util.Properties;

@Configuration
public class KafkaStreamInventoryService {

    @Value("${kafka.broker}")
    private String bootstrapAddress;

    @Value("${kafka.stream.applicationid}")
    private String applicationid;

    @Value("${kafka.topic.order}")
    private String topic;

    @Bean
    public KafkaStreams kafkaStreams(){
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationid);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass());
        final KafkaStreams kafkaStreams = new KafkaStreams(kafkaStreamTopology(), props);
        kafkaStreams.start();

        return kafkaStreams;
    }


    @Bean
    public Topology kafkaStreamTopology(){
        Serde<JsonNode> jsonSerde = Serdes.serdeFrom(new JsonSerializer(), new JsonDeserializer());
        final StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<Integer, JsonNode> order = streamsBuilder.stream(topic, Consumed.with(Serdes.Integer(), jsonSerde));
        KStream<Integer, JsonNode> updatedOrder = order.filter((key, value) -> notUpdated(value))
                                                       .mapValues(value -> updateStatus(value));

        updatedOrder.to(topic, Produced.with(Serdes.Integer(),jsonSerde));

        return streamsBuilder.build();
    }

    public void setTopic(String topic){
        this.topic = topic;
    }

    private boolean notUpdated(JsonNode value){
        ObjectNode order = (ObjectNode)value;
        if(!order.has("status")) {
            return true;
        }
        return false;
    }

    private JsonNode updateStatus(JsonNode value){
        ObjectNode order = (ObjectNode)value;
        order.put("status", "fulfilled");
        return order;
    }

}