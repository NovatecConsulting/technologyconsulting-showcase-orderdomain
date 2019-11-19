package de.novatec.showcase.controller;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;

abstract public class ResourcdITBase {

	protected static final String PORT = System.getProperty("http.port");
	protected static final String BASE_URL = "http://localhost:" + PORT + "/orderdomain/";
	protected Client client;

	protected void assertResponse200(String url, Response response) {
		assertEquals("Incorrect response code from " + url, Response.Status.OK.getStatusCode(), response.getStatus());
	}

	protected void assertResponse201(String url, Response response) {
		assertEquals("Incorrect response code from " + url, Response.Status.CREATED.getStatusCode(),
				response.getStatus());
	}

	protected void assertResponse404(String url, Response response) {
		assertEquals("Incorrect response code from " + url, Response.Status.NOT_FOUND.getStatusCode(),
				response.getStatus());
	}

	@Before
	public void setup() {
		client = ClientBuilder.newClient();
	}

	@After
	public void teardown() {
		client.close();
	}

}
