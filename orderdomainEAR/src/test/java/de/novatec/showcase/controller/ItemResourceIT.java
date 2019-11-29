package de.novatec.showcase.controller;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import de.novatec.showcase.dto.Item;

public class ItemResourceIT extends ResourcdITBase {

	@Test
	public void testGetItemWithNonExistingId() {
		WebTarget target = client.target(ITEM_URL).path(NON_EXISTING_ID);
		Response response = asTestUser(target.request()).get();
		assertResponse200(ITEM_URL, response);
		
			assertEquals("Result should be an empty json array!", 0, response.readEntity(new GenericType<List<Item>>() {}).size());
	}

	@Test
	public void testGetItemWithId() {
		WebTarget target = client.target(ITEM_URL).path(testItem.getId());
		Response response = asTestUser(target.request()).get();
		assertResponse200(ITEM_URL, response);

			List<Item> items = response.readEntity(new GenericType<List<Item>>() {});
		assertEquals("Result should be just one element in an json array!", 1, items.size());
		assertEquals(testItem, items.get(0));
	}
}