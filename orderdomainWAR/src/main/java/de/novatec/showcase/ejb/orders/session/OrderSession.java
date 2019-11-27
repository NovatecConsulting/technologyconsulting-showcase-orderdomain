package de.novatec.showcase.ejb.orders.session;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import de.novatec.showcase.ejb.orders.entity.Customer;
import de.novatec.showcase.ejb.orders.entity.Item;
import de.novatec.showcase.ejb.orders.entity.Order;
import de.novatec.showcase.ejb.orders.entity.OrderLine;
import de.novatec.showcase.ejb.orders.entity.OrderStatus;
import de.novatec.showcase.ejb.orders.entity.ShoppingCart;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class OrderSession implements OrderSessionRemote, OrderSessionLocal {

	private static final int MAX_SHOPPING_CART_SIZE = 200;

	private static final int MIN_SHOPPING_CART_SIZE = 20;

	@PersistenceContext
	private EntityManager em;

	@Resource
	private SessionContext sessionContext;

	@EJB
	private CustomerSessionLocal customerService;

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Order getOrder(int id) {
		return em.find(Order.class, id);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Long getOrderCount(Integer customerId) {
		Query orderCount = em.createNamedQuery("COUNT_BY_CUSTOMER");
		orderCount.setParameter("id", customerId);
		@SuppressWarnings("unchecked")
		List<Long> elementList = orderCount.getResultList();
		return elementList != null ? elementList.get(0).longValue() : Long.valueOf(0).longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Order> getOpenOrders(Integer customerId) {
		Query orderQuery = em.createNamedQuery("QUERY_BY_CUSTOMER_AND_ORDER_STATUS");
		orderQuery.setParameter("id", customerId);
		orderQuery.setParameter("status", OrderStatus.DEFERRED);
		return orderQuery.getResultList();
	}

	@Override
	public Integer newOrder(Integer customerId, ShoppingCart shoppingCart) throws InsufficientCreditException {
		Customer customer = this.customerService.getCustomer(customerId);
		if (customer == null) {
			throw new RuntimeException("Customer with id " + customerId + " does not exist!");
		}

		BigDecimal costs = shoppingCart.getTotalPrice();
		
		// Add price of open (known) Orders to the price of cart.
		// This is needed because the balance of the Customer wont be adjusted
		// until the ordered parts arrive in the inventory (see addInventory(..)
		// in CustomerSession).
		for (Order order : this.getOpenOrders(customerId)) {
			OrderStatus orderStatus = order.getStatus();
			if (!orderStatus.equals(OrderStatus.UNKNOWN) && !orderStatus.equals(OrderStatus.DELETED)) {
				costs = costs.add(order.getTotal());
			}
		}

		if (!customer.hasSufficientCredit(costs)) {
			throw new InsufficientCreditException("Customer (ID: " + customer.getId()
					+ ") has not enough credit to cover this purchase! (Costs: " + costs + " Balance: "
					+ customer.getBalance() + " CreditLimit: " + customer.getCreditLimit());
		}

		Order order = new Order(shoppingCart.getTotalPrice(), shoppingCart.getTotalDiscount(), customer);
		em.persist(order);
//		em.flush();

		int lineNumber = 1;
		for (Item item : shoppingCart.getItems()) {
			OrderLine orderLine = new OrderLine(lineNumber, order.getId(), shoppingCart.getQuantity(item), shoppingCart.getPrice(item),
					item.getPrice(), order, item);
			em.persist(orderLine);
			em.flush();
			order.addOrderLine(orderLine);
			lineNumber++;
		}

		if(!order.isPriceWithDiscountEqualTotal())
		{
			throw new RuntimeException("Calculated price with discount is NOT equal total price!");
		}
		if(!order.isPriceMinusDiscountEqualPriceWithDiscount())
		{
			throw new RuntimeException("calculated price minus discount is NOT equal calculated price with discount!");
		}

		int shoppingCartSize = shoppingCart.getItemCount();
		if (shoppingCartSize > MIN_SHOPPING_CART_SIZE && shoppingCartSize <= MAX_SHOPPING_CART_SIZE) {
			// TODO place a large order
			// this will be a wrapper client for (multiple) rest calls
			// make a call for each Orderline of the Order
		} else if (shoppingCartSize <= MIN_SHOPPING_CART_SIZE) {
			// its a small order
			// TODO Call Customer.addInventory(Order) if not an OrderStatsu.DEFFERED
			// purchase
		} else {
			throw new RuntimeException("Size of the ShoppingCart violates the specification. Cart size: " + shoppingCartSize);
		}
		return order.getId();
	}

	@Override
	public void cancelOrder(int orderId) {
		Order order = this.getOrder(orderId);
		if (order != null) {
			order.setStatus(OrderStatus.DELETED);
		}

	}
	
}
