package de.novatec.showcase.controller;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import de.novatec.showcase.controller.helper.JsonHelper;
import de.novatec.showcase.ejb.orders.entity.Address;
import de.novatec.showcase.ejb.orders.entity.Customer;
import de.novatec.showcase.ejb.orders.entity.Item;
import de.novatec.showcase.ejb.orders.entity.Order;
import de.novatec.showcase.ejb.orders.entity.OrderStatus;

public class OrderResourceIT extends ResourcdITBase {

	private static Item testItem = null;
	private static Integer customerId = null;
	private static Integer orderId = null;

	@BeforeClass
	public static void beforeClass() {
		// create the test customer
		WebTarget target = client.target(CUSTOMER_URL);
		Customer customer = new Customer("firstname", "lastname", "contact", "credit", new BigDecimal(1000.0),
				constantDate(), new BigDecimal(100.0), new BigDecimal(10.0), null,
				new Address("street1", "street2", "city", "state", "county", "zip", "phone"));
		Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.json(JsonHelper.toJson(customer)));
		assertResponse201(ORDER_URL, response);

		JSONObject json = new JSONObject(response.readEntity(String.class));
		customerId = Integer.valueOf(json.getInt("id"));

		// create the testItem
		target = client.target(ITEM_URL);
		testItem = new Item("name", "description", new BigDecimal(100.0), new BigDecimal(0.0), 1, 0);
		response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.json(JsonHelper.toJson(testItem)));
		assertResponse201(ITEM_URL, response);

		json = new JSONObject(response.readEntity(String.class));
		Integer itemId = Integer.valueOf(json.getInt("id"));
		testItem.setId(itemId.toString());

		// create the test Order
		target = client.target(ORDER_URL).path(customerId.toString());
		ItemQuantityPairs itemQuantityPairs = new ItemQuantityPairs()
				.setItemQuantityPairs(Arrays.asList(new ItemQuantityPair(testItem, 1)));
		response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.json(JsonHelper.toJson(itemQuantityPairs)));
		assertResponse201(ORDER_URL, response);

		json = new JSONObject(response.readEntity(String.class));
		orderId = Integer.valueOf(json.getInt("id"));
	}

	@Test
	public void testGetOrderWithNonExistingId() {
		WebTarget target = client.target(ORDER_URL).path(NON_EXISTING_ID);
		Response response = target.request().get();
		assertResponse404(ORDER_URL, response);
		String errorMessage = response.readEntity(String.class);
		assertEquals("Wrong result from Response object!", "Order with id '" + NON_EXISTING_ID + "' not found!",
				errorMessage);
	}

	@Test
	public void testGetCustomerWithIdLowerThan1() {
		WebTarget target = client.target(ORDER_URL).path("0");
		Response response = target.request().get();
		assertResponse500(ORDER_URL, response);
		String errorMessage = response.readEntity(String.class);
		assertEquals("Wrong result from Response object!", "Id cannot be less than 1!", errorMessage);
	}

	@Test
	public void testGetOrderWithId() {
		WebTarget target = client.target(ORDER_URL).path(orderId.toString());
		Response response = target.request().get();
		assertResponse200(ORDER_URL, response);

		Order order = JsonHelper.fromJsonOrder(response.readEntity(String.class));

		// TODO more test with deserialized order
		assertEquals(1, order.getOrderLines().size());
	}

	@Test
	public void testDeleteOrder() {
		// create the an other customer
		WebTarget target = client.target(CUSTOMER_URL);
		Customer customer = new Customer("firstname", "lastname", "contact", "credit", new BigDecimal(1000.0),
				constantDate(), new BigDecimal(100.0), new BigDecimal(10.0), null,
				new Address("street1", "street2", "city", "state", "county", "zip", "phone"));
		Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.json(JsonHelper.toJson(customer)));
		assertResponse201(ORDER_URL, response);

		JSONObject json = new JSONObject(response.readEntity(String.class));
		Integer otherCustomerId = Integer.valueOf(json.getInt("id"));

		// create the Order to be deleted
		target = client.target(ORDER_URL).path(otherCustomerId.toString());
		ItemQuantityPairs itemQuantityPairs = new ItemQuantityPairs()
				.setItemQuantityPairs(Arrays.asList(new ItemQuantityPair(testItem, 1)));
		response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.json(JsonHelper.toJson(itemQuantityPairs)));
		assertResponse201(ORDER_URL, response);

		json = new JSONObject(response.readEntity(String.class));
		Integer orderIdToBeDeleted = Integer.valueOf(json.getInt("id"));

		target = client.target(ORDER_URL).path(orderIdToBeDeleted.toString());
		response = target.request(MediaType.APPLICATION_JSON).delete();
		assertResponse200(ORDER_URL, response);

		target = client.target(ORDER_URL).path(orderIdToBeDeleted.toString());
		response = target.request().get();
		assertResponse200(ORDER_URL, response);
		Order order = JsonHelper.fromJsonOrder(response.readEntity(String.class));
		assertEquals("Order status has to be DELETED!", OrderStatus.DELETED, order.getStatus());
	}

	@Test
	public void testGetOpenOrders() {
		WebTarget target = client.target(ORDER_URL).path("open_orders_by_customer/" + customerId.toString());
		Response response = target.request().get();
		assertResponse200(ORDER_URL, response);
		JSONArray jsonOpenOrders = new JSONArray(response.readEntity(String.class));
		assertEquals("There should be only one open order with customer " + customerId + "!", 1,
				jsonOpenOrders.length());
	}

	@Test
	public void testCountByCustomer() {
		WebTarget target = client.target(ORDER_URL).path("count_bycustomer/" + customerId.toString());
		Response response = target.request().get();
		assertResponse200(ORDER_URL, response);
		JSONObject jsonCount = new JSONObject(response.readEntity(String.class));
		assertEquals("Size should be only one order with customer " + customerId + "!", Integer.valueOf(1),
				Integer.valueOf(jsonCount.getString("count")));
	}
}
