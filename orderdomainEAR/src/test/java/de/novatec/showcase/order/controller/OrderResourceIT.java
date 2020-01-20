package de.novatec.showcase.order.controller;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import de.novatec.showcase.order.dto.ItemQuantityPair;
import de.novatec.showcase.order.dto.ItemQuantityPairs;
import de.novatec.showcase.order.dto.Order;
import de.novatec.showcase.order.dto.OrderStatus;

public class OrderResourceIT extends ResourceITBase {

	@Test
	public void testLargeOrder()
	{
		WebTarget target = client.target(ORDER_URL).path(createCustomer().getId().toString());
		ItemQuantityPairs itemQuantityPairs = new ItemQuantityPairs()
				.setItemQuantityPairs(Arrays.asList(
						new ItemQuantityPair(createItem(), 1), 
						new ItemQuantityPair(createItem(), 1), 
						new ItemQuantityPair(createItem(), 1), 
						new ItemQuantityPair(createItem(), 1), 
						new ItemQuantityPair(createItem(), 1), 
						new ItemQuantityPair(createItem(), 1), 
						new ItemQuantityPair(createItem(), 1), 
						new ItemQuantityPair(createItem(), 1), 
						new ItemQuantityPair(createItem(), 1), 
						new ItemQuantityPair(createItem(), 1), 
						new ItemQuantityPair(createItem(), 1), 
						new ItemQuantityPair(createItem(), 1), 
						new ItemQuantityPair(createItem(), 1), 
						new ItemQuantityPair(createItem(), 1), 
						new ItemQuantityPair(createItem(), 1), 
						new ItemQuantityPair(createItem(), 1), 
						new ItemQuantityPair(createItem(), 1),
						new ItemQuantityPair(createItem(), 1),
						new ItemQuantityPair(createItem(), 1),
						new ItemQuantityPair(createItem(), 1),
						new ItemQuantityPair(createItem(), 1)));
		Builder builder = asAdmin(target.request(MediaType.APPLICATION_JSON));
		Response response = builder.accept(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(itemQuantityPairs));
		assertResponse201(ORDER_URL, response);
	}
	
	
	@Test
	public void testGetOrderWithNonExistingId() {
		WebTarget target = client.target(ORDER_URL).path(NON_EXISTING_ID);
		Response response = asTestUser(target.request()).get();
		assertResponse404(ORDER_URL, response);
		String errorMessage = response.readEntity(String.class);
		assertEquals("Wrong result from Response object!", "Order with id '" + NON_EXISTING_ID + "' not found!",
				errorMessage);
	}

	@Test
	public void testGetCustomerWithIdLowerThan1() {
		WebTarget target = client.target(ORDER_URL).path("0");
		Response response = asTestUser(target.request()).get();
		assertResponse500(ORDER_URL, response);
		String errorMessage = response.readEntity(String.class);
		assertEquals("Wrong result from Response object!", "Id cannot be less than 1!", errorMessage);
	}

	@Test
	public void testGetOrderWithId() {
		WebTarget target = client.target(ORDER_URL).path(testOrder.getId().toString());
		Response response = asTestUser(target.request()).get();
		assertResponse200(ORDER_URL, response);
		assertEquals(testOrder, response.readEntity(Order.class));
	}

	@Test
	public void testDeleteOrder() {
		// create an order for an other customer which has to be deleted
		Order orderIdBeDeleted = createOrder(createCustomer().getId(), testItem);
		WebTarget target = client.target(ORDER_URL).path(orderIdBeDeleted.getId().toString());
		Response response = asAdmin(target.request(MediaType.APPLICATION_JSON)).delete();
		assertResponse200(ORDER_URL, response);
		Order deletedOrder = response.readEntity(Order.class);
		assertEquals("Order status has to be DELETED!", OrderStatus.DELETED, deletedOrder.getStatus());
	}

	@Test
	public void testGetOpenOrders() {
		WebTarget target = client.target(ORDER_URL).path("open_orders_by_customer/" + testCustomer.getId().toString());
		Response response = asTestUser(target.request()).get();
		assertResponse200(ORDER_URL, response);
		List<Order> openOrders = response.readEntity(new GenericType<List<Order>>() {});
		assertEquals("There should be only one open order with customer " + testCustomer + "!", 1,
				openOrders.size());
	}

	@Test
	public void testCountByCustomer() {
		WebTarget target = client.target(ORDER_URL).path("count_bycustomer/" + testCustomer.getId().toString());
		Response response = asTestUser(target.request()).get();
		assertResponse200(ORDER_URL, response);
		assertEquals("Size should be only one order with customer " + testCustomer + "!", 1,
		response.readEntity(JsonObject.class).getInt("count"));
	}

}
