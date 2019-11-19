package de.novatec.showcase.controller;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.novatec.showcase.ejb.orders.entity.Customer;
import de.novatec.showcase.ejb.orders.entity.Item;
import de.novatec.showcase.ejb.orders.entity.Order;

public class JsonHelper {
	private ObjectMapper mapper = new ObjectMapper();

	public JsonHelper() {
	}

	String toJsonString(Object object) {
		String json = "{}";
		try {

			json = mapper.writeValueAsString(object);
		}

		catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}

	String toJson(List<?> entities) {
		return toJsonString(entities);
	}

	String toJson(Item item) {
		return toJsonString(item);
	}

	String toJson(Customer customer) {
		return toJsonString(customer);
	}

	String toJson(Order order) {
		return toJsonString(order);
	}
}