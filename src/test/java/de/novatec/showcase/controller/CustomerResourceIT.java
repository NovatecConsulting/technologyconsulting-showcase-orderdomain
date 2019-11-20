package de.novatec.showcase.controller;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.junit.Test;

import de.novatec.showcase.controller.helper.JsonHelper;
import de.novatec.showcase.ejb.orders.entity.Address;
import de.novatec.showcase.ejb.orders.entity.Customer;

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
		assertJsonCustomer(testCustomer, new JSONObject(response.readEntity(String.class)));
	}
}
