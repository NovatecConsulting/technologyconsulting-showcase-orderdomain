package de.novatec.showcase.controller.helper;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.novatec.showcase.ejb.orders.entity.Customer;
import de.novatec.showcase.ejb.orders.entity.Item;
import de.novatec.showcase.ejb.orders.entity.Order;

public abstract class JsonHelper {
	private static ObjectMapper mapper = new ObjectMapper();

	private JsonHelper() {
	}

	public static String toJsonString(Object object) {
		String json = "{}";
		try {

			json = mapper.writeValueAsString(object);
		}

		catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}

	public static String toJson(List<?> entities) {
		return toJsonString(entities);
	}

	public static String toJson(Item item) {
		return toJsonString(item);
	}

	public static String toJson(Customer customer) {
		return toJsonString(customer);
	}

	public static String toJson(Order order) {
		return toJsonString(order);
	}
}