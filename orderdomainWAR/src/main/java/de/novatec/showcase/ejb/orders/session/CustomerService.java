package de.novatec.showcase.ejb.orders.session;

import java.math.BigDecimal;
import java.util.List;

import de.novatec.showcase.ejb.orders.entity.Customer;
import de.novatec.showcase.ejb.orders.entity.CustomerInventory;
import de.novatec.showcase.ejb.orders.entity.Order;
import de.novatec.showcase.ejb.orders.entity.OrderLine;

public interface CustomerService {
	public Customer getCustomer(Integer customerId);

	public long countCustomer();

	public boolean validateCustomer(Integer customerId);

	public List<CustomerInventory> getInventories(Integer customerId);

	public List<Customer> selectCustomerWithGoodCredit();

	public boolean checkCustomerCredit(Integer customerId, BigDecimal costs);

	public boolean sellInventory(Integer customerId, String itemId, int quantity);

	public void addInventory(Order order);

	public void addInventory(OrderLine orderLine);

	public void addInventory(Integer orderId);

	public Integer createCustomer(Customer customer);

}
