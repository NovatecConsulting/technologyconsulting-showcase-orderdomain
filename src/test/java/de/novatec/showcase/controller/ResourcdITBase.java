package de.novatec.showcase.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import de.novatec.showcase.ejb.orders.entity.Address;
import de.novatec.showcase.ejb.orders.entity.Customer;
import de.novatec.showcase.ejb.orders.entity.Item;

abstract public class ResourcdITBase {

	protected static final String PORT = System.getProperty("http.port");
	protected static final String BASE_URL = "http://localhost:" + PORT + "/orderdomain/";
	protected static final String NON_EXISTING_ID = "1000";
	protected static final String ORDER_URL = BASE_URL + "order/";
	protected static final String ITEM_URL = BASE_URL + "item/";
	protected static final String CUSTOMER_URL = BASE_URL + "customer/";
	protected static Client client;

	public static void assertResponse200(String url, Response response) {
		assertEquals("Incorrect response code from " + url, Response.Status.OK.getStatusCode(), response.getStatus());
	}

	public static void assertResponse201(String url, Response response) {
		assertEquals("Incorrect response code from " + url, Response.Status.CREATED.getStatusCode(),
				response.getStatus());
	}

	public static void assertResponse404(String url, Response response) {
		assertEquals("Incorrect response code from " + url, Response.Status.NOT_FOUND.getStatusCode(),
				response.getStatus());
	}

	public static void assertResponse500(String url, Response response) {
		assertEquals("Incorrect response code from " + url, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				response.getStatus());
	}

	protected static Calendar constantDate() {
		Calendar calendar = Calendar.getInstance(Locale.GERMAN);
		calendar.set(Calendar.YEAR, 2019);
		calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 20);
		return calendar;
	}

	protected static String format(Calendar calendar) {
		return new SimpleDateFormat(Customer.DATE_FORMAT, Locale.GERMAN).format(calendar.getTime());
	}

	protected static void assertJsonItem(Item expectedItem, JSONObject actualJsonItem) {
		assertEquals("Name is not equal!", expectedItem.getName(), actualJsonItem.getString("name"));
		assertEquals("Description is not equal!", expectedItem.getDescription(),
				actualJsonItem.getString("description"));
		assertEquals("Price is not equal!", expectedItem.getPrice(), actualJsonItem.getBigDecimal("price"));
		assertEquals("Discount is not equal!", expectedItem.getDiscount(), actualJsonItem.getBigDecimal("discount"));
		assertEquals("Category is not equal!", expectedItem.getCategory(), actualJsonItem.getInt("category"));
		assertNotEquals("Version is equal!", expectedItem.getVersion(), actualJsonItem.getInt("version"));
		assertNotNull("Id is null", Integer.valueOf(actualJsonItem.getInt("id")));
		assertNotEquals("Id is equal!", expectedItem.getId(), actualJsonItem.getInt("id"));
	}

	protected static void assertJsonCustomer(Customer expectedCustomer, JSONObject actualJsonCustomer) {
		assertEquals("FirstName is not equal!", expectedCustomer.getFirstName(),
				actualJsonCustomer.getString("firstName"));
		assertEquals("LastName is not equal!", expectedCustomer.getLastName(),
				actualJsonCustomer.getString("lastName"));
		assertEquals("Contact is not equal!", expectedCustomer.getContact(), actualJsonCustomer.getString("contact"));
		assertEquals("Credit is not equal!", expectedCustomer.getCredit(), actualJsonCustomer.getString("credit"));
		assertEquals("CreditLimit is not equal!", expectedCustomer.getCreditLimit(),
				actualJsonCustomer.getBigDecimal("creditLimit"));
		assertEquals("Balance is not equal!", expectedCustomer.getBalance(),
				actualJsonCustomer.getBigDecimal("balance"));
		assertEquals("YtdPayment is not equal!", expectedCustomer.getYtdPayment(),
				actualJsonCustomer.getBigDecimal("ytdPayment"));
		assertEquals("Calendar is not equal!", format(expectedCustomer.getSince()), actualJsonCustomer.get("since"));
		assertEquals("CustomerInventory length is not equal!", 0,
				actualJsonCustomer.getJSONArray("customerInventories").length());

		assertJsonAddress(expectedCustomer.getAddress(), actualJsonCustomer.getJSONObject("address"));

		assertNotEquals("Version is equal!", expectedCustomer.getVersion(), actualJsonCustomer.getInt("version"));
		assertNotNull("Id is null", Integer.valueOf(actualJsonCustomer.getInt("id")));
		assertNotEquals("Id is equal!", expectedCustomer.getId(), Integer.valueOf(actualJsonCustomer.getInt("id")));
	}

	protected static void assertJsonAddress(Address expectedAddress, JSONObject actualJsonAddress) {
		assertEquals("Address is not equal!", expectedAddress.getStreet1(), actualJsonAddress.getString("street1"));
		assertEquals("Address is not equal!", expectedAddress.getStreet2(), actualJsonAddress.getString("street2"));
		assertEquals("Address is not equal!", expectedAddress.getCity(), actualJsonAddress.getString("city"));
		assertEquals("Address is not equal!", expectedAddress.getState(), actualJsonAddress.getString("state"));
		assertEquals("Address is not equal!", expectedAddress.getCountry(), actualJsonAddress.getString("country"));
		assertEquals("Address is not equal!", expectedAddress.getZip(), actualJsonAddress.getString("zip"));
		assertEquals("Address is not equal!", expectedAddress.getPhone(), actualJsonAddress.getString("phone"));
	}

	@BeforeClass
	public static void setup() {
		client = ClientBuilder.newClient();
	}

	@AfterClass
	public static void teardown() {
		client.close();
	}

}
