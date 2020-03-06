package de.novatec.showcase.order.inventory.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;


/*
    This service consumer json from 'order' topic,
    add a new field 'status' with value 'fulfilled' to the json
    and publish it back to the 'order' topic.
*/

@Deprecated
//@Service
public class InventoryService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    KafkaTemplate<Integer, JsonNode> kafkaTemplate;

    @KafkaListener(topics = "${kafka.topic.order}")
    public void orderListener(
            @Payload ObjectNode order,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Integer key,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.OFFSET) Integer offset,
            Acknowledgment acknowledgment) {
        Boolean updated = updateStatus(order);
        if(updated) {
            ListenableFuture<SendResult<Integer, JsonNode>> future = kafkaTemplate.send(topic, key, order);
            future.addCallback(new ListenableFutureCallback<SendResult<Integer, JsonNode>>() {
                @Override
                public void onSuccess(SendResult<Integer, JsonNode> result) {
                    acknowledgment.acknowledge(); //Manually commit the offset after successfully writing the order with status back to Kafka
                }
                @Override
                public void onFailure(Throwable ex) {
                    logger.warn("Cannot write order record back to Kafka, retrying",ex);
                    acknowledgment.nack(10);
                }
            });
        }else{
            acknowledgment.acknowledge();
        }
    }

    public boolean updateStatus(ObjectNode order){
        if(!order.get("status").asText().equals("FULFILLED")) {
            order.put("status", "FULFILLED");
            return true;
        }
        return false;
    }
}
