package de.novatec.showcase.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
	
	private static final String URL = BASE_URL + "customer/";

	@Test
	public void testGetCustomerWithId1000WhichDoesNotExist() {
		WebTarget target = client.target(URL).path("1000");
		Response response = target.request().get();
		assertResponse404(URL, response);
		String errorMessage = response.readEntity(String.class);
		assertEquals("Wrong result from Response object!", "Customer with id '1000' not found!", errorMessage);
	}

	@Test
	public void testGetCustomerWithId() {
		WebTarget target = client.target(URL);
		Customer customer = new Customer("firstname", "lastname", "contact", "credit", new BigDecimal(1000.0), constantDate(), new BigDecimal(100.0),
				new BigDecimal(10.0), null, new Address("street1", "street2", "city", "state", "county", "zip", "phone"));
		Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.json(JsonHelper.toJson(customer)));
		assertResponse201(URL, response);

		JSONObject json = new JSONObject(response.readEntity(String.class));
		Integer customerId = Integer.valueOf(json.getInt("id"));
		target = client.target(URL).path(customerId.toString());
		response = target.request().get();
		assertResponse200(URL, response);
		JSONObject jsonCustomer = new JSONObject(response.readEntity(String.class));
		assertEquals("FirstName is not equal!", customer.getFirstName(), jsonCustomer.getString("firstName"));
		assertEquals("LastName is not equal!", customer.getLastName(), jsonCustomer.getString("lastName"));
		assertEquals("Contact is not equal!", customer.getContact(), jsonCustomer.getString("contact"));
		assertEquals("Credit is not equal!", customer.getCredit(), jsonCustomer.getString("credit"));
		assertEquals("CreditLimit is not equal!", customer.getCreditLimit(), jsonCustomer.getBigDecimal("creditLimit"));
		assertEquals("Balance is not equal!", customer.getBalance(), jsonCustomer.getBigDecimal("balance"));
		assertEquals("YtdPayment is not equal!", customer.getYtdPayment(), jsonCustomer.getBigDecimal("ytdPayment"));
		assertEquals("Calendar is not equal!", format(customer.getSince()), 
				jsonCustomer.get("since"));
		assertEquals("CustomerInventory length is not equal!", 0, jsonCustomer.getJSONArray("customerInventories").length());
		JSONObject addressJson = jsonCustomer.getJSONObject("address");
		assertEquals("Address is not equal!", customer.getAddress().getStreet1(), addressJson.getString("street1"));
		assertEquals("Address is not equal!", customer.getAddress().getStreet2(), addressJson.getString("street2"));
		assertEquals("Address is not equal!", customer.getAddress().getCity(), addressJson.getString("city"));
		assertEquals("Address is not equal!", customer.getAddress().getState(), addressJson.getString("state"));
		assertEquals("Address is not equal!", customer.getAddress().getCountry(), addressJson.getString("country"));
		assertEquals("Address is not equal!", customer.getAddress().getZip(), addressJson.getString("zip"));
		assertEquals("Address is not equal!", customer.getAddress().getPhone(), addressJson.getString("phone"));
		assertNotEquals("Version is not equal!", customer.getVersion(), jsonCustomer.getInt("version"));
		assertNotEquals("Id is equal!", customer.getId(), Integer.valueOf(jsonCustomer.getInt("id")));
	}

	private String format(Calendar calendar) {
		return new SimpleDateFormat(Customer.DATE_FORMAT, Locale.GERMAN).format(calendar.getTime());
	}
	
	private Calendar constantDate() {
		Calendar calendar = Calendar.getInstance(Locale.GERMAN);
		calendar.set(Calendar.YEAR, 2019);
		calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 20);
		return calendar;
	}
}
