package de.novatec.showcase.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import de.novatec.showcase.controller.helper.JsonHelper;
import de.novatec.showcase.ejb.orders.entity.Address;
import de.novatec.showcase.ejb.orders.entity.Customer;
import de.novatec.showcase.ejb.orders.entity.Item;
import de.novatec.showcase.ejb.orders.entity.Order;

abstract public class ResourcdITBase {

	protected static final String PORT = System.getProperty("http.port");
	protected static final String BASE_URL = "http://localhost:" + PORT + "/orderdomain/";
	protected static final String NON_EXISTING_ID = "1000";
	protected static final String ORDER_URL = BASE_URL + "order/";
	protected static final String ITEM_URL = BASE_URL + "item/";
	protected static final String CUSTOMER_URL = BASE_URL + "customer/";
	protected static Client client;
	protected static Item testItem = null;
	protected static Customer testCustomer = null;
	protected static Order testOrder = null;

	@BeforeClass
	public static void beforeClass() {
		client = ClientBuilder.newClient();
		testCustomer = createCustomer();
		testItem = createItem();
		testOrder = createOrder(testCustomer.getId(), testItem);
	}

	@AfterClass
	public static void teardown() {
		client.close();
	}
	
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

	protected static Order createOrder(Integer customerId, Item item) {
		WebTarget target = client.target(ORDER_URL).path(customerId.toString());
		ItemQuantityPairs itemQuantityPairs = new ItemQuantityPairs()
				.setItemQuantityPairs(Arrays.asList(new ItemQuantityPair(item, 1)));
		Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.json(JsonHelper.toJson(itemQuantityPairs)));
		assertResponse201(ORDER_URL, response);
	
		JSONObject json = new JSONObject(response.readEntity(String.class));
		target = client.target(ORDER_URL).path(Integer.valueOf(json.getInt("id")).toString());
		response = target.request().get();
		assertResponse200(ORDER_URL, response);
	
		return JsonHelper.fromJsonOrder(response.readEntity(String.class));
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

	protected static Customer createCustomer() {
		WebTarget target = client.target(CUSTOMER_URL);
		Customer customer = new Customer("firstname", "lastname", "contact", "credit", new BigDecimal(1000.0),
				constantDate(), new BigDecimal(100.0), new BigDecimal(10.0), null,
				new Address("street1", "street2", "city", "state", "county", "zip", "phone"));
		Response response = target.request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.json(JsonHelper.toJson(customer)));
		assertResponse201(ORDER_URL, response);
		
		JSONObject json = new JSONObject(response.readEntity(String.class));
		target = client.target(CUSTOMER_URL).path(Integer.valueOf(json.getInt("id")).toString());
		response = target.request().get();
		assertResponse200(CUSTOMER_URL, response);
		return JsonHelper.fromJsonCustomer(new JSONObject(response.readEntity(String.class)).toString());
	}

}
