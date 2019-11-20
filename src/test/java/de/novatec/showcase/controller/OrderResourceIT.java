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
		// create an order for an other customer which has to be deleted
		Integer orderIdToBeDeleted = createOrderId(createCustomerId(), testItem);
		WebTarget target = client.target(ORDER_URL).path(orderIdToBeDeleted.toString());
		Response response = target.request(MediaType.APPLICATION_JSON).delete();
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

	@BeforeClass
	public static void beforeClass() {
		customerId = createCustomerId();
		testItem = createItem();
		orderId = createOrderId(customerId, testItem);
	}

	private static Integer createOrderId(Integer customerId, Item item) {
		WebTarget target = client.target(ORDER_URL).path(customerId.toString());
		ItemQuantityPairs itemQuantityPairs = new ItemQuantityPairs()
				.setItemQuantityPairs(Arrays.asList(new ItemQuantityPair(item, 1)));
		Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.json(JsonHelper.toJson(itemQuantityPairs)));
		assertResponse201(ORDER_URL, response);

		JSONObject json = new JSONObject(response.readEntity(String.class));
		return Integer.valueOf(json.getInt("id"));
	}

	private static Item createItem() {
		WebTarget target = client.target(ITEM_URL);
		Item item = new Item("name", "description", new BigDecimal(100.0), new BigDecimal(0.0), 1, 0);
		Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.json(JsonHelper.toJson(item)));
		assertResponse201(ITEM_URL, response);

		JSONObject json = new JSONObject(response.readEntity(String.class));
		target = client.target(ITEM_URL).path(Integer.valueOf(json.getInt("id")).toString());
		response = target.request().get();
		assertResponse200(ITEM_URL, response);

		JSONArray jsonItems = new JSONArray(response.readEntity(String.class));
		assertEquals("Result should be just one element in an json array!", 1, jsonItems.length());

		return JsonHelper.fromJsonItem(jsonItems.getJSONObject(0).toString());
	}

	private static Integer createCustomerId() {
		WebTarget target = client.target(CUSTOMER_URL);
		Customer customer = new Customer("firstname", "lastname", "contact", "credit", new BigDecimal(1000.0),
				constantDate(), new BigDecimal(100.0), new BigDecimal(10.0), null,
				new Address("street1", "street2", "city", "state", "county", "zip", "phone"));
		Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.json(JsonHelper.toJson(customer)));
		assertResponse201(ORDER_URL, response);

		JSONObject json = new JSONObject(response.readEntity(String.class));
		return Integer.valueOf(json.getInt("id"));
	}

}
