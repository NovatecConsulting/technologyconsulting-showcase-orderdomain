package de.novatec.showcase.order.ejb.session;

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
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.novatec.showcase.order.GlobalConstants;
import de.novatec.showcase.order.client.manufacture.RestcallException;
import de.novatec.showcase.order.client.manufacture.WorkOrderScheduler;
import de.novatec.showcase.order.dto.WorkOrder;
import de.novatec.showcase.order.ejb.entity.Customer;
import de.novatec.showcase.order.ejb.entity.Order;
import de.novatec.showcase.order.ejb.entity.OrderLine;
import de.novatec.showcase.order.ejb.entity.OrderStatus;
import de.novatec.showcase.order.ejb.entity.ShoppingCart;
import de.novatec.showcase.order.ejb.session.exception.InsufficientCreditException;
import de.novatec.showcase.order.ejb.session.exception.PriceException;
import de.novatec.showcase.order.ejb.session.exception.SpecificationException;
import de.novatec.showcase.order.mapper.DtoMapper;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class OrderSession implements OrderSessionLocal {
	
	private static Logger log = LoggerFactory.getLogger(OrderSession.class);

	private static final int MAX_SHOPPING_CART_SIZE = 200;

	private static final int MIN_SHOPPING_CART_SIZE = 20;

	@PersistenceContext
	private EntityManager em;

	@Resource
	private SessionContext sessionContext;

	@EJB
	private CustomerSessionLocal customerService;
	
	private WorkOrderScheduler workOrderScheduler = new WorkOrderScheduler();

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Order getOrder(int id) {
		return em.find(Order.class, id);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Long getOrderCount(Integer customerId) {
		TypedQuery<Long> orderCount = em.createNamedQuery(Order.COUNT_BY_CUSTOMER, Long.class);
		orderCount.setParameter("id", customerId);
		List<Long> elementList = orderCount.getResultList();
		return elementList != null ? elementList.get(0).longValue() : Long.valueOf(0).longValue();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Order> getOpenOrders(Integer customerId) {
		TypedQuery<Order> orderQuery = em.createNamedQuery(Order.BY_CUSTOMER_AND_ORDER_STATUS, Order.class);
		orderQuery.setParameter("id", customerId);
		orderQuery.setParameter("status", OrderStatus.DEFERRED);
		return orderQuery.getResultList();
	}

	@Override
	public Integer newOrder(Integer customerId, ShoppingCart shoppingCart) throws InsufficientCreditException, PriceException, SpecificationException, RestcallException {
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

		int lineNumber = 1;

		for (de.novatec.showcase.order.dto.Item item : shoppingCart.getItems()) {
			OrderLine orderLine = new OrderLine(lineNumber, order.getId(), shoppingCart.getQuantity(item), shoppingCart.getPrice(item),
					item.getPrice(), order, DtoMapper.mapToItemEntity(item));
			em.persist(orderLine);
			order.addOrderLine(orderLine);
			order = em.merge(order);
			lineNumber++;
		}

		if(!order.isPriceWithDiscountEqualTotal())
		{
			throw new PriceException("Calculated price with discount is NOT equal total price!");
		}
		if(!order.isPriceMinusDiscountEqualPriceWithDiscount())
		{
			throw new PriceException("Calculated price minus discount is NOT equal calculated price with discount!");
		}

		int shoppingCartSize = shoppingCart.getItemCount();
		if (shoppingCartSize > MIN_SHOPPING_CART_SIZE && shoppingCartSize <= MAX_SHOPPING_CART_SIZE) {
			if(!GlobalConstants.IS_SINGLE_EAR_DEPLOYMENT)
			{
				
			
			// TODO place a large order
			// this will be a wrapper client for (multiple) rest calls
			// make a call for each Orderline of the Order
			// manufaturedomain.scheduleWorkOrder(WorkOrder) for each OrderLine -> 
			// WorkOrder(wo.location=1, wo.salesId = pk.orderid, wo.orderLineId=pk.number, wo.originalQantity=quantity, wo.assemblyId=getItem.getId, wo.dueDate=Calendar.getInstance()) 
			// call advanceWorkOrderStatus 3 times
			// call completeWorkOrder
			// call CustomerSerssion.addInventory(OrderLIne) (read Orderline first by id from WorkOrder.salesId)
			// build RestCalls with Object analog to the one in the Domain which could bes serialized to json which will be used by the corresponding domain
			
				log.info("Scheduling large order for:");
				for (OrderLine orderLine : order.getOrderLines()) {

					try {
						log.info(orderLine.toString());
						WorkOrder workOrder = workOrderScheduler.schedule(orderLine);
					} catch (RestcallException e) {
						log.error(e.getMessage());
						throw e;
					}
					// do more with the workorder? or trigger actions via REST like described above? What about setting OrderStatus.PENDING_MANUFACTURE?
				}
			}
			else
			{
				log.info("OrderdomainEAR is deployed as a single EAR -> calls to manfaturedomain.scheduleWorkOrder are ignored!");
			}
		} else if (shoppingCartSize <= MIN_SHOPPING_CART_SIZE) {
			// this should be done by a the corresponding REST call 
//			customerService.addInventory(order);
		} else {
			String message = "Size of the ShoppingCart violates the specification. Cart size: " + shoppingCartSize;
			log.error(message);
			throw new SpecificationException(message);
		}
		return order.getId();
	}

	@Override
	public void cancelOrder(Integer orderId) {
		Order order = this.getOrder(orderId);
		if (order != null) {
			order.setStatus(OrderStatus.DELETED);
		}
	}
	
}
