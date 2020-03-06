package de.novatec.showcase.order.inventory.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;


public class KafkaStreamInventoryServiceTest {
    private TopologyTestDriver testDriver;
    private ObjectMapper objectMapper;

    private String topic="test";

    @Before
    public void setUp(){
        KafkaStreamInventoryService processor = new KafkaStreamInventoryService();
        processor.setTopic(topic);
        Topology topology = processor.kafkaStreamTopology();

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test-stream-processor");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass());
        testDriver = new TopologyTestDriver(topology, props);
        objectMapper = new ObjectMapper();
    }

    @After
    public void tearDown(){
        testDriver.close();
    }

    @Test
    public void kafkaStreamTopology() throws JsonProcessingException {
        TestInputTopic<Integer, JsonNode> inputTopic = testDriver.createInputTopic(topic, new IntegerSerializer(), new JsonSerializer());
        String jsonString = "{ \"status\" : \"DEFERRED\", \"k2\" : \"v2\" }";
        JsonNode order = objectMapper.readTree(jsonString);
        inputTopic.pipeInput(1,order);

        TestOutputTopic<Integer, JsonNode> outputTopic = testDriver.createOutputTopic(topic, new IntegerDeserializer(), new JsonDeserializer());
        ObjectNode output = (ObjectNode)outputTopic.readValue();

        String status = output.get("status").asText();
        assertEquals("FULFILLED",status);
    }
}