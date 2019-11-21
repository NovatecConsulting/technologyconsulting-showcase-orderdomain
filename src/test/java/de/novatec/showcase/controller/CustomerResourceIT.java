package de.novatec.showcase.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import de.novatec.showcase.controller.helper.JsonHelper;
import de.novatec.showcase.ejb.orders.entity.Customer;
import de.novatec.showcase.ejb.orders.entity.Order;

public class CustomerResourceIT extends ResourcdITBase {

	@Test
	public void testGetCustomerWithNonExistingId() {
		WebTarget target = client.target(CUSTOMER_URL).path(NON_EXISTING_ID);
		Response response = target.request().get();
		assertResponse404(CUSTOMER_URL, response);
		String errorMessage = response.readEntity(String.class);
		assertEquals("Wrong result from Response object!", "Customer with id '" + NON_EXISTING_ID + "' not found!",
				errorMessage);
	}

	@Test
	public void testGetCustomerWithId() {
		WebTarget target = client.target(CUSTOMER_URL).path(testCustomer.getId().toString());
		Response response = target.request().get();
		assertResponse200(CUSTOMER_URL, response);
		assertEquals(testCustomer, JsonHelper.fromJsonCustomer(response.readEntity(String.class)));
	}

	@Test
	public void testGetCustomerCount() {
		WebTarget target = client.target(CUSTOMER_URL).path("count");
		Response response = target.request().get();
		assertResponse200(CUSTOMER_URL, response);
		assertTrue("There should be 1 Customer at a minimum!",
				new JSONObject(response.readEntity(String.class)).getInt("count") >= 1);
	}

	@Test
	public void testCustomerWithGoodCredit() {
		WebTarget target = client.target(CUSTOMER_URL).path("with_good_credit");
		Response response = target.request().get();
		assertResponse200(CUSTOMER_URL, response);
		assertTrue("There should be 1 Customer at a minimum!",
				new JSONArray(response.readEntity(String.class)).length() >= 1);
	}

	@Test
	public void testCustomerExist() {
		WebTarget target = client.target(CUSTOMER_URL).path("exist/" + testCustomer.getId().toString());
		Response response = target.request().get();
		assertResponse200(CUSTOMER_URL, response);
		assertTrue("Customer with id " + testCustomer.getId() + " should exist!",
				new JSONObject(response.readEntity(String.class)).getBoolean("exist"));
	}

	@Test
	public void testAddInventories() {
		// create a new customer
		Customer customer = createCustomer();
		// and a new order
		Order order = createOrder(customer.getId(), testItem);

		// check if inventories is empty
		WebTarget target = client.target(CUSTOMER_URL).path("inventories/" + customer.getId().toString());
		Response response = target.request().get();
		assertResponse404(CUSTOMER_URL, response);
		assertEquals("Customer with id '" + customer.getId() + "' has no inventory!",
				response.readEntity(String.class));

		// call add_Inventories
		target = client.target(CUSTOMER_URL).path("add_inventory/" + order.getId().toString());
		response = target.request().put(Entity.json(JsonHelper.toJson(customer)));
		assertResponse200(CUSTOMER_URL, response);

		// check if inventories contains the number of items in order
		target = client.target(CUSTOMER_URL).path("inventories/" + customer.getId().toString());
		response = target.request().get();
		assertResponse200(CUSTOMER_URL, response);
		assertEquals("Customer with id '" + customer.getId() + "' should should have 1 entry!", 1,
				new JSONArray(response.readEntity(String.class)).length());
	}

	@Test
	public void testSellInventories() {
		// create a new customer
		Customer customer = createCustomer();
		// and a new order
		Order order = createOrder(customer.getId(), testItem);

		// check if inventories is empty
		WebTarget target = client.target(CUSTOMER_URL).path("inventories/" + customer.getId().toString());
		Response response = target.request().get();
		assertResponse404(CUSTOMER_URL, response);
		assertEquals("Customer with id '" + customer.getId() + "' has no inventory!",
				response.readEntity(String.class));

		// call add_Inventories
		target = client.target(CUSTOMER_URL).path("add_inventory/" + order.getId().toString());
		response = target.request().put(Entity.json(JsonHelper.toJson(customer)));
		assertResponse200(CUSTOMER_URL, response);

		// check if inventories contains the number of items in order
		target = client.target(CUSTOMER_URL).path("inventories/" + customer.getId().toString());
		response = target.request().get();
		assertResponse200(CUSTOMER_URL, response);
		assertEquals("Customer with id '" + customer.getId() + "' should should have 1 entry!", 1,
				new JSONArray(response.readEntity(String.class)).length());

		// call sell inventories
		Integer quantity = Integer.valueOf("1");
		target = client.target(CUSTOMER_URL)
				.path("sell_inventory/" + customer.getId().toString() + "/" + testItem.getId() + "/" + quantity);
		response = target.request().put(Entity.json(JsonHelper.toJson(customer)));
		assertResponse200(CUSTOMER_URL, response);

		// there should be no entry in inventory - no inventory for customer
		target = client.target(CUSTOMER_URL).path("inventories/" + customer.getId().toString());
		response = target.request().get();
		assertResponse404(CUSTOMER_URL, response);
		assertEquals("Customer with id '" + customer.getId() + "' has no inventory!",
				response.readEntity(String.class));
	}

	@Test
	public void testCheckCreditOfCustomerWithCosts() {
		BigDecimal costs = new BigDecimal(10.0);
		WebTarget target = client.target(CUSTOMER_URL)
				.path("check_credit/" + testCustomer.getId().toString() + "/" + costs);
		Response response = target.request().get();
		assertResponse200(CUSTOMER_URL, response);
		assertTrue("Customer should have credit!",
				new JSONObject(response.readEntity(String.class)).getBoolean("credit"));

	}

}
