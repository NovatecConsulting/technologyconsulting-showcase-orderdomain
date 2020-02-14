package de.novatec.showcase.order.inventory.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;



class InventoryServiceTest {

    InventoryService inventoryService = new InventoryService();
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testUpdateStatus() throws Exception {
        String jsonString = "{ \"k1\" : \"v1\", \"k2\" : \"v2\" }";
        JsonNode order = objectMapper.readTree(jsonString);
        Boolean updated = inventoryService.updateStatus((ObjectNode)order);
        assertTrue(order.has("status"));
        String status = order.get("status").asText();
        assertEquals("fulfilled",status);
        jsonString = "{ \"status\" : \"v1\", \"k2\" : \"v2\" }";
        order = objectMapper.readTree(jsonString);
        updated = inventoryService.updateStatus((ObjectNode)order);
        assertFalse(updated);
    }
}