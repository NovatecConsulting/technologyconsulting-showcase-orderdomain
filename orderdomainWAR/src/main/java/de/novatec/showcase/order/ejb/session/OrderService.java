package de.novatec.showcase.order.ejb.session;

import java.util.List;

import de.novatec.showcase.order.ejb.entity.Order;
import de.novatec.showcase.order.ejb.entity.ShoppingCart;

public interface OrderService {
	public Order getOrder(int id);

	public Long getOrderCount(Integer customerId);

	public List<Order> getOpenOrders(Integer customerId);

	public Integer newOrder(Integer customerId, ShoppingCart cart) throws InsufficientCreditException;

	public void cancelOrder(Integer orderId);
}
