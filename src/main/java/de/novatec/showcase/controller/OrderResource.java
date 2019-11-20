package de.novatec.showcase.controller;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.novatec.showcase.controller.helper.JsonHelper;
import de.novatec.showcase.ejb.orders.entity.Order;
import de.novatec.showcase.ejb.orders.entity.ShoppingCart;
import de.novatec.showcase.ejb.orders.session.InsufficientCreditException;
import de.novatec.showcase.ejb.orders.session.OrderSessionLocal;

@ManagedBean
@Path(value = "/order")
public class OrderResource {

	@EJB
	private OrderSessionLocal bean;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "{id}")
	public Response getOrder(@PathParam("id") int orderId) {
		if (orderId <= 0) {
			return Response.serverError().entity("Id cannot be less than 1!").build();
		}
		Order order = bean.getOrder(orderId);
		if (order == null) {
			return Response.status(Response.Status.NOT_FOUND).entity("Order with id '" + orderId + "' not found!")
					.build();
		}
		String json = JsonHelper.toJson(order);
		return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "count_bycustomer/{id}")
	public Response countByCustomer(@PathParam("id") Integer customerId) {
		if (customerId <= 0) {
			return Response.serverError().entity("Id cannot be less than 1!").build();
		}
		long count = bean.getOrderCount(customerId);
		String json = "{ \"count\": \"" + count + "\" }";
		return Response.ok(json).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "open_orders_by_customer/{id}")
	public Response getOpenOrders(@PathParam("id") Integer customerId) {
		if (customerId <= 0) {
			return Response.serverError().entity("Id cannot be less than 1!").build();
		}
		List<Order> orders = bean.getOpenOrders(customerId);
		if (orders == null || orders.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND)
					.entity("Orders with customer id '" + customerId + "' not found").build();
		}
		String json = JsonHelper.toJson(orders);
		return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path(value = "{customerId}")
	public Response createOrder(@PathParam("customerId") Integer customerId, ItemQuantityPairs itemQuantityPairs,
			@Context UriInfo uriInfo) {
		// TODO validate parameters customerId and itemQuantityPairs
		ShoppingCart shoppingCart = new ShoppingCart();
		for (ItemQuantityPair itemQuantityPair : itemQuantityPairs.getItemQuantityPairs()) {
			shoppingCart.addItem(itemQuantityPair.getItem(), itemQuantityPair.getQuantity());
		}
		Integer id;
		try {
			id = bean.newOrder(customerId, shoppingCart);
		} catch (InsufficientCreditException e) {
			return Response.status(Response.Status.PRECONDITION_FAILED)
					.entity("The customer with id '" + customerId + "' has insufficient credit!").build();
		}
		String json = "{ \"id\": \"" + id + "\" }";
		return Response.created(uriInfo.getAbsolutePathBuilder().build()).entity(json).build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "{id}")
	public Response deleteOrder(@PathParam("id") int orderId) {
		// TODO cancel method should return the deleted (or marked as deleted) order id
		// and if the id is not found an exception should be thrown, so that the
		// Response with Response.Status.NOT_FOUND could returned
		bean.cancelOrder(orderId);
		return Response.ok().build();
	}
}