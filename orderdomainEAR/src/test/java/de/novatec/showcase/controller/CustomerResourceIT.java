package de.novatec.showcase.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import de.novatec.showcase.dto.Customer;
import de.novatec.showcase.dto.CustomerInventory;
import de.novatec.showcase.dto.Order;

public class CustomerResourceIT extends ResourcdITBase {

	@Test
	public void testGetCustomerWithNonExistingId() {
		WebTarget target = client.target(CUSTOMER_URL).path(NON_EXISTING_ID);
		Response response = asTestUser(target.request()).get();
		assertResponse404(CUSTOMER_URL, response);
		String errorMessage = response.readEntity(String.class);
		assertEquals("Wrong result from Response object!", "Customer with id '" + NON_EXISTING_ID + "' not found!",
				errorMessage);
	}

	@Test
	public void testGetCustomerWithId() {
		WebTarget target = client.target(CUSTOMER_URL).path(testCustomer.getId().toString());
		Response response = asTestUser(target.request(MediaType.APPLICATION_JSON_TYPE)).get();
		assertResponse200(CUSTOMER_URL, response);
		assertEquals(testCustomer, response.readEntity(Customer.class));
	}

	@Test
	public void testGetCustomerCount() {
		WebTarget target = client.target(CUSTOMER_URL).path("count");
		Response response = asTestUser(target.request()).get();
		assertResponse200(CUSTOMER_URL, response);
		assertTrue("There should be 1 Customer at a minimum!",
		response.readEntity(JsonObject.class).getInt("count") >= 1);
	}

	@Test
	public void testCustomerWithGoodCredit() {
		WebTarget target = client.target(CUSTOMER_URL).path("with_good_credit");
		Response response = asTestUser(target.request()).get();
		assertResponse200(CUSTOMER_URL, response);
		assertTrue("There should be 1 Customer at a minimum!", response.readEntity(new GenericType<List<Customer>>() {}).size() >= 1);
	}

	@Test
	public void testCustomerExist() {
		WebTarget target = client.target(CUSTOMER_URL).path("exist/" + testCustomer.getId().toString());
		Response response = asTestUser(target.request()).get();
		assertResponse200(CUSTOMER_URL, response);
		assertTrue("Customer with id " + testCustomer.getId() + " should exist!",
		response.readEntity(JsonObject.class).getBoolean("exist"));
	}

	@Test
	public void testAddInventories() {
		// create a new customer
		Customer customer = createCustomer();
		// and a new order
		Order order = createOrder(customer.getId(), testItem);

		// check if inventories is empty
		WebTarget target = client.target(CUSTOMER_URL).path("inventories/" + customer.getId().toString());
		Response response = asTestUser(target.request()).get();
		assertResponse404(CUSTOMER_URL, response);
		assertEquals("Customer with id '" + customer.getId() + "' has no inventory!",
		response.readEntity(String.class));

		// call add_Inventories
		target = client.target(CUSTOMER_URL).path("add_inventory/" + order.getId().toString());
		response = asTestUser(target.request()).put(Entity.json(customer));
		assertResponse200(CUSTOMER_URL, response);

		// check if inventories contains the number of items in order
		target = client.target(CUSTOMER_URL).path("inventories/" + customer.getId().toString());
		response = asTestUser(target.request()).get();
		assertResponse200(CUSTOMER_URL, response);
		assertEquals("Customer with id '" + customer.getId() + "' should should have 1 entry!", 1,
		response.readEntity(new GenericType<List<CustomerInventory>>() {}).size());
	}

	@Test
	public void testSellInventories() {
		// create a new customer
		Customer customer = createCustomer();
		// and a new order
		Order order = createOrder(customer.getId(), testItem);

		// check if inventories is empty
		WebTarget target = client.target(CUSTOMER_URL).path("inventories/" + customer.getId().toString());
		Response response = asTestUser(target.request()).get();
		assertResponse404(CUSTOMER_URL, response);
		assertEquals("Customer with id '" + customer.getId() + "' has no inventory!",
		response.readEntity(String.class));

		// call add_Inventories
		target = client.target(CUSTOMER_URL).path("add_inventory/" + order.getId().toString());
		response = asTestUser(target.request()).put(Entity.json(customer));
		assertResponse200(CUSTOMER_URL, response);

		// check if inventories contains the number of items in order
		target = client.target(CUSTOMER_URL).path("inventories/" + customer.getId().toString());
		response = asTestUser(target.request()).get();
		assertResponse200(CUSTOMER_URL, response);
		assertEquals("Customer with id '" + customer.getId() + "' should should have 1 entry!", 1,
		response.readEntity(new GenericType<List<CustomerInventory>>() {}).size());

		// call sell inventories
		Integer quantity = Integer.valueOf("1");
		target = client.target(CUSTOMER_URL)
				.path("sell_inventory/" + customer.getId().toString() + "/" + testItem.getId() + "/" + quantity);
		response = asTestUser(target.request()).put(Entity.json(customer));
		assertResponse200(CUSTOMER_URL, response);
		assertTrue("Inventory should be sold!", response.readEntity(JsonObject.class).getBoolean("sold"));

		// there should be no entry in inventory - no inventory for customer
		target = client.target(CUSTOMER_URL).path("inventories/" + customer.getId().toString());
		response = asTestUser(target.request()).get();
		assertResponse404(CUSTOMER_URL, response);
		assertEquals("Customer with id '" + customer.getId() + "' has no inventory!",
		response.readEntity(String.class));
	}

	@Test
	public void testCheckCreditOfCustomerWithCosts() {
		BigDecimal costs = new BigDecimal(10.0);
		WebTarget target = client.target(CUSTOMER_URL)
				.path("check_credit/" + testCustomer.getId().toString() + "/" + costs);
		Response response = asTestUser(target.request()).get();
		assertResponse200(CUSTOMER_URL, response);
		assertTrue("Customer should have credit!", response.readEntity(JsonObject.class).getBoolean("credit"));
	}

}
