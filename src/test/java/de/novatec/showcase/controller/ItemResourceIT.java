package de.novatec.showcase.controller;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.junit.Test;

import de.novatec.showcase.controller.helper.JsonHelper;

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
		WebTarget target = client.target(ITEM_URL).path(testItem.getId());
		Response response = target.request().get();
		assertResponse200(ITEM_URL, response);

		JSONArray jsonItems = new JSONArray(response.readEntity(String.class));
		assertEquals("Result should be just one element in an json array!", 1, jsonItems.length());
		assertEquals(testItem, JsonHelper.fromJsonItem(jsonItems.getJSONObject(0).toString()));
	}
}
