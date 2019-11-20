package de.novatec.showcase.controller.helper;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.novatec.showcase.controller.ItemQuantityPairs;
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

	public static Object fromJsonString(String json, Class<?> type) {
		Object converted = null;
		try {
			converted =  mapper.readValue(json, type);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return converted;
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

	public static String toJson(ItemQuantityPairs itemQuantityPairs) {
		return toJsonString(itemQuantityPairs);
	}
	
	public static Order fromJsonOrder(String jsonOrder) {
		return (Order)fromJsonString(jsonOrder, Order.class);
	}

	public static Item fromJsonItem(String jsonItem) {
		return (Item)fromJsonString(jsonItem, Item.class);
	}
	
	public static Customer fromJsonCustomer(String jsonCustomer) {
		return (Customer)fromJsonString(jsonCustomer, Customer.class);
	}
}