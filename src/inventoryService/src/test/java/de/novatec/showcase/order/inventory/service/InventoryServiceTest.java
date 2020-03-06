package de.novatec.showcase.order.inventory.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;



public class InventoryServiceTest {

    InventoryService inventoryService = new InventoryService();
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testUpdateStatus() throws Exception {
        String jsonString = "{ \"status\" : \"DEFERRED\", \"k2\" : \"v2\" }";
        JsonNode order = objectMapper.readTree(jsonString);
        Boolean updated = inventoryService.updateStatus((ObjectNode)order);
        assertTrue(updated);
        String status = order.get("status").asText();
        assertEquals("FULFILLED",status);
        jsonString = "{ \"status\" : \"FULFILLED\", \"k2\" : \"v2\" }";
        order = objectMapper.readTree(jsonString);
        updated = inventoryService.updateStatus((ObjectNode)order);
        assertFalse(updated);
    }
}