package de.novatec.showcase.order.ejb.session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import de.novatec.showcase.order.ejb.entity.Customer;
import de.novatec.showcase.order.ejb.entity.CustomerInventory;
import de.novatec.showcase.order.ejb.entity.Item;
import de.novatec.showcase.order.ejb.entity.Order;
import de.novatec.showcase.order.ejb.entity.OrderLine;
import de.novatec.showcase.order.ejb.entity.OrderStatus;
import de.novatec.showcase.order.ejb.session.exception.CustomerNotFoundException;
import de.novatec.showcase.order.ejb.session.exception.ItemNotFoundException;
import de.novatec.showcase.order.ejb.session.exception.OrderNotFoundException;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CustomerSession implements CustomerSessionLocal {

	@Resource
	SessionContext mySessionCtx;

	@PersistenceContext
	EntityManager em;

	@Override
	public Customer getCustomer(Integer customerId) throws CustomerNotFoundException {
		Customer customer = em.find(Customer.class, customerId);
		if (customer == null) {
			throw new CustomerNotFoundException("Customer with id " + customerId + " does not exist!");
		}
		return customer;
	}

	@Override
	public long countCustomer() {
		TypedQuery<Long> queryCustomerCount = em.createNamedQuery(Customer.COUNT_CUSTOMERS, Long.class);
		return queryCustomerCount.getSingleResult();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<CustomerInventory> getInventories(Integer customerId) throws CustomerNotFoundException {
		Customer customer = this.getCustomer(customerId);
		if (customer == null) {
			return new ArrayList<CustomerInventory>();
		}
		em.refresh(customer);
		return customer.getCustomerInventories();
	}

	@Override
	public List<Customer> selectCustomerWithGoodCredit() {
		TypedQuery<Customer> creditQuery = em.createNamedQuery(Customer.CUSTOMERS_BY_CREDIT, Customer.class);
		creditQuery.setParameter("credit", "GC");
		return creditQuery.getResultList();
	}

	@Override
	public boolean checkCustomerCredit(Integer customerId, BigDecimal costs) {
		Customer customer;
		try {
			customer = this.getCustomer(customerId);
		} catch (CustomerNotFoundException e) {
			return false;
		}
		if (customer == null) {
			return false;
		}
		return customer.hasSufficientCredit(costs);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean sellInventory(Integer customerId, String itemId, int quantity) throws ItemNotFoundException, CustomerNotFoundException {
		Item item = em.find(Item.class, itemId);
		if (item == null) {
			throw new ItemNotFoundException("The Items with id " + itemId + " was not found!");
		}

		// get CustomerInventory for the Item with the Id 'itemId'
		CustomerInventory customerInventory = this.getInventoryItem(this.getCustomer(customerId), item);
		if (customerInventory == null) {
			return false;
		}

		if (customerInventory.getQuantity() - quantity >= 0) {
			BigDecimal priceInclDiscount = item.getPrice().subtract(item.getDiscount());

			// reduce quantity in Inventory
			customerInventory.addQuantity(-1 * quantity, priceInclDiscount.multiply(new BigDecimal(-1)));

			// add money to the balance of customer
			Customer customer = this.getCustomer(customerId);
			customer.addBalance(priceInclDiscount.multiply(new BigDecimal(quantity)));

			if (customerInventory.getQuantity() == 0) {
				em.remove(customerInventory);

			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void addInventory(Order order) {
		for (OrderLine orderLine : order.getOrderLines()) {
			this.addInventory(orderLine);
		}
		order.setStatus(OrderStatus.SHIPPED);
		order.setShipDate(Calendar.getInstance());
	}

	// should this query really select orders in all states??? DEFERRED,
	// PENDING_MANUFACTUR
	// should be criteria
	// for me it looks like the call is not idempotent, but it should be in a REST
	// (PATCH) call
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void addInventory(Integer orderId) throws OrderNotFoundException {
		Order order = em.find(Order.class, orderId);
		if (order == null) {
			throw new OrderNotFoundException("The order with id " + orderId + " was not found!");
		}
		this.addInventory(order);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void addInventory(OrderLine orderLine) {
		orderLine.setStatus(OrderStatus.SHIPPED);

		BigDecimal totalPrice = getTotalPrice(orderLine);

		// add manufactured items to the corresponding CustomerInventory
		Customer customer = orderLine.getOrder().getCustomer();

		CustomerInventory customerInventory = this.getInventoryItem(customer, orderLine.getItem());
		if (customerInventory != null) {
			customerInventory.addQuantity(orderLine.getQuantity(), totalPrice);
		} else {

			customerInventory = new CustomerInventory(customer, orderLine.getItem(), totalPrice,
					orderLine.getQuantity());
			em.persist(customerInventory);
			customer.addInventory(customerInventory);
		}
		customer.reduceBalance(totalPrice);
		completeOrder(orderLine.getOrder().getId());
	}

	private BigDecimal getTotalPrice(OrderLine orderLine) {
		Item item = orderLine.getItem();
		BigDecimal priceInclDiscount = item.getPrice().subtract(item.getDiscount());
		BigDecimal totalPrice = priceInclDiscount.multiply(new BigDecimal(orderLine.getQuantity()));
		return totalPrice;
	}

	/**
	 * complete the Order by setting the status to OrderStatus.SHIPPED if all
	 * OrderLines of the Order have been shipped
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private void completeOrder(Integer orderId) {
		Order order = em.find(Order.class, orderId);
		if (order == null || order.getStatus().equals(OrderStatus.SHIPPED)) {
			return;
		}

		for (OrderLine orderLine : order.getOrderLines()) {
			if (!orderLine.getStatus().equals(OrderStatus.SHIPPED)) {
				return;
			}
		}
		order.setStatus(OrderStatus.SHIPPED);
	}

	public CustomerInventory getInventoryItem(Customer customer, Item item) {
		for (CustomerInventory customerInventory : customer.getCustomerInventories()) {
			if (customerInventory.getVehicle().equals(item)) {
				return customerInventory;
			}
		}
		return null;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Customer createCustomer(Customer customer) {
		em.persist(customer);
		return customer;
	}

}
