package de.novatec.showcase.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.math.BigDecimal;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.novatec.showcase.controller.JsonHelper;
import de.novatec.showcase.ejb.orders.entity.Item;

public class ItemResourceIT {

	String url = "http://localhost:9080/orderdomain/item/";

	private Client client;

	@Before
	public void setup() {
		client = ClientBuilder.newClient();
	}

	@After
	public void teardown() {
		client.close();
	}

	@Test
	public void testGetItemsWithId1000WhichDoesNotExist() {
		WebTarget target = client.target(url).path("1000");
		Response response = target.request().get();
		assertResponse200(url, response);
		String items = response.readEntity(String.class);
		JSONArray jsonItems = new JSONArray(response.readEntity(String.class));
		assertEquals("Result should be an empty json array!",0, jsonItems.length());
	}

	@Test
	public void testGetItemsWithId1() {
		WebTarget target = client.target(url);
		Item item = new Item("name", "description", new BigDecimal(100.0), new BigDecimal(0.0), 1, 0);
		Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.json(new JsonHelper().toJson(item)));
		assertResponse201(url, response);

		JSONObject json = new JSONObject(response.readEntity(String.class));
		Integer itemId = Integer.valueOf(json.getInt("id"));
		target = client.target(url).path(itemId.toString());
		response = target.request().get();
		assertResponse200(url, response);
		JSONArray jsonItems = new JSONArray(response.readEntity(String.class));
		assertEquals("Result should be just one element in an json array!", 1, jsonItems.length());
		JSONObject jsonObject = jsonItems.getJSONObject(0);
		assertEquals("Name is not equal!", item.getName(), jsonObject.getString("name"));
		assertEquals("Description is not equal!", item.getDescription(), jsonObject.getString("description"));
		assertEquals("Price is not equal!", item.getPrice(), jsonObject.getBigDecimal("price"));
		assertEquals("Discount is not equal!", item.getDiscount(), jsonObject.getBigDecimal("discount"));
		assertEquals("Category is not equal!", item.getCategory(), jsonObject.getInt("category"));
		assertEquals("Version is not equal!", item.getVersion(), jsonObject.getInt("version"));
		assertNotEquals("Id is equal!", item.getId(), jsonObject.getInt("id"));
	}

	private void assertResponse200(String url, Response response) {
		assertEquals("Incorrect response code from " + url, Response.Status.OK.getStatusCode(), response.getStatus());
	}

	private void assertResponse201(String url, Response response) {
		assertEquals("Incorrect response code from " + url, Response.Status.CREATED.getStatusCode(),
				response.getStatus());
	}

	private void assertResponse404(String url, Response response) {
		assertEquals("Incorrect response code from " + url, Response.Status.NOT_FOUND.getStatusCode(),
				response.getStatus());
	}
}
