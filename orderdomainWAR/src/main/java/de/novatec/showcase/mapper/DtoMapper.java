package de.novatec.showcase.mapper;

import java.util.List;

import de.novatec.showcase.dto.Customer;
import de.novatec.showcase.dto.CustomerInventory;
import de.novatec.showcase.dto.Item;
import de.novatec.showcase.dto.Order;
import de.novatec.showcase.dto.OrderLine;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

abstract public class DtoMapper {
	private static MapperFactory mapperFactory;
	private static MapperFacade mapper;

	static
	{
		mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.classMap(CustomerInventory.class, de.novatec.showcase.ejb.orders.entity.CustomerInventory.class ).exclude("customer").byDefault().register();
		mapperFactory.classMap(Customer.class, de.novatec.showcase.ejb.orders.entity.Customer.class ).exclude("customerInventories{customer}").byDefault().register();
		mapperFactory.classMap(OrderLine.class, de.novatec.showcase.ejb.orders.entity.OrderLine.class ).exclude("order").byDefault().register();
		mapperFactory.classMap(Order.class, de.novatec.showcase.ejb.orders.entity.Order.class ).exclude("orderLines{order}").byDefault().register();
		mapper = mapperFactory.getMapperFacade();
	}
	
	public static List<Item> mapToItemDto(List<de.novatec.showcase.ejb.orders.entity.Item> items) {
		return mapper.mapAsList(items, Item.class);
	}

	public static List<Customer> mapToCustomerDto(List<de.novatec.showcase.ejb.orders.entity.Customer> customers) {
		return mapper.mapAsList(customers, Customer.class);
	}

	public static Customer mapToCustomerDto(de.novatec.showcase.ejb.orders.entity.Customer customer) {
		return mapper.map(customer, Customer.class);
	}

	public static List<Order> mapToOrderDto(List<de.novatec.showcase.ejb.orders.entity.Order> orders) {
		return mapper.mapAsList(orders, Order.class);
	}
	

	public static Order mapToOrderDto(de.novatec.showcase.ejb.orders.entity.Order order) {
		return mapper.map(order, Order.class);
	}
	
	public static List<CustomerInventory> mapToCustomerInventoryDto(
			List<de.novatec.showcase.ejb.orders.entity.CustomerInventory> customerinventories) {
		return mapper.mapAsList(customerinventories, CustomerInventory.class);
	}

	public static de.novatec.showcase.ejb.orders.entity.Item mapToItemEntity(Item item) {
		return mapper.map(item, de.novatec.showcase.ejb.orders.entity.Item.class);
	}

	public static de.novatec.showcase.ejb.orders.entity.Customer mapToCustomerEntity(Customer customer) {
		return mapper.map(customer, de.novatec.showcase.ejb.orders.entity.Customer.class);
	}
}
