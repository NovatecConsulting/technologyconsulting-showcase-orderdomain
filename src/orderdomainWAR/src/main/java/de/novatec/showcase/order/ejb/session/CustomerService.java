package de.novatec.showcase.order.ejb.session;

import java.math.BigDecimal;
import java.util.List;

import de.novatec.showcase.order.ejb.entity.Customer;
import de.novatec.showcase.order.ejb.entity.CustomerInventory;
import de.novatec.showcase.order.ejb.entity.Order;
import de.novatec.showcase.order.ejb.entity.OrderLine;
import de.novatec.showcase.order.ejb.session.exception.CustomerNotFoundException;
import de.novatec.showcase.order.ejb.session.exception.ItemNotFoundException;
import de.novatec.showcase.order.ejb.session.exception.OrderNotFoundException;

public interface CustomerService {
	public Customer getCustomer(Integer customerId) throws CustomerNotFoundException;

	public long countCustomer();

	public List<CustomerInventory> getInventories(Integer customerId) throws CustomerNotFoundException;

	public List<Customer> selectCustomerWithGoodCredit();

	public boolean checkCustomerCredit(Integer customerId, BigDecimal costs);

	public boolean sellInventory(Integer customerId, String itemId, int quantity) throws ItemNotFoundException, CustomerNotFoundException;

	public void addInventory(Order order);

	public void addInventory(OrderLine orderLine);

	public void addInventory(Integer orderId) throws OrderNotFoundException;

	public Customer createCustomer(Customer customer);

}
