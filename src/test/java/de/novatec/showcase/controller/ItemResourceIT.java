package de.novatec.showcase.controller;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import de.novatec.showcase.controller.helper.JsonHelper;
import de.novatec.showcase.ejb.orders.entity.Item;

public class ItemResourceIT extends ResourcdITBase {

	@Test
	public void testGetItemWithNonExistingId() {
		WebTarget target = client.target(ITEM_URL).path(NON_EXISTING_ID);
		Response response = target.request().get();
		assertResponse200(ITEM_URL, response);
		JSONArray jsonItems = new JSONArray(response.readEntity(String.class));
		assertEquals("Result should be an empty json array!", 0, jsonItems.length());
	}

	@Test
	public void testGetItemWithId() {
		WebTarget target = client.target(ITEM_URL);
		Item item = new Item("name", "description", new BigDecimal(100.0), new BigDecimal(0.0), 1, 0);
		Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.json(JsonHelper.toJson(item)));
		assertResponse201(ITEM_URL, response);

		JSONObject json = new JSONObject(response.readEntity(String.class));
		Integer itemId = Integer.valueOf(json.getInt("id"));
		target = client.target(ITEM_URL).path(itemId.toString());
		response = target.request().get();
		assertResponse200(ITEM_URL, response);

		JSONArray jsonItems = new JSONArray(response.readEntity(String.class));
		assertEquals("Result should be just one element in an json array!", 1, jsonItems.length());
		assertJsonItem(item, jsonItems.getJSONObject(0));
	}
}
