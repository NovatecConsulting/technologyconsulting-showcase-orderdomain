package de.novatec.showcase.order.inventory.kafka;


import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;

import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;


public class KafkaStreamInventoryServiceTest {
    private TopologyTestDriver testDriver;
    Serde<GenericRecord> serde;

    private String topic="test";

    private static final String MOCK_SCHEMA_REGISTRY_URL = "mock://testurl";

    @Before
    public void setUp(){
        KafkaStreamInventoryService processor = new KafkaStreamInventoryService();
        processor.setTopic(topic);
        Topology topology = processor.kafkaStreamTopology();

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test-stream-processor");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class);
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, MOCK_SCHEMA_REGISTRY_URL);

        testDriver = new TopologyTestDriver(topology, props);

        //Configure the test serde to use mock schema registry
        serde = new GenericAvroSerde();
        Map<String, String> config = new HashMap<>();
        config.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, MOCK_SCHEMA_REGISTRY_URL);
        serde.configure(config, false);
    }

    @After
    public void tearDown(){
        testDriver.close();
        serde.close();
    }

    @Test
    public void avroKafkaStreamTopology(){
        String stringSchema = "{ \"type\": \"record\"," +
                "\"name\": \"test\"," +
                "\"fields\": [" +
                "{\"name\": \"status\"," +
                "\"type\": {\"type\": \"enum\"," +
                "\"name\":\"OrderStatus\"," +
                "\"symbols\":[\"DEFERRED\",\"PENDING_MANUFACTUR\",\"SHIPPED\",\"DELETED\",\"UNKNOWN\",\"FULFILLED\"]}}]}";
        Schema avroSchema = new Schema.Parser().parse(stringSchema);
        GenericRecordBuilder builder = new GenericRecordBuilder(avroSchema);
        builder.set("status", new GenericData.EnumSymbol(avroSchema.getField("status").schema(), "DEFERRED"));
        GenericData.Record record = builder.build();

        TestInputTopic<Integer, GenericRecord> inputTopic = testDriver.createInputTopic(topic, new IntegerSerializer(), serde.serializer());
        inputTopic.pipeInput(1,record);

        TestOutputTopic<Integer, GenericRecord> outputTopic = testDriver.createOutputTopic(topic, new IntegerDeserializer(), serde.deserializer());
        GenericRecord output = outputTopic.readValue();
        String status = output.get("status").toString();
        assertEquals("FULFILLED",status);
    }
}