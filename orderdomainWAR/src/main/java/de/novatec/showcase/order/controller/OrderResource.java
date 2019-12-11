package de.novatec.showcase.order.controller;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
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

import de.novatec.showcase.order.GlobalConstants;
import de.novatec.showcase.order.dto.ItemQuantityPair;
import de.novatec.showcase.order.dto.ItemQuantityPairs;
import de.novatec.showcase.order.dto.Order;
import de.novatec.showcase.order.ejb.entity.ShoppingCart;
import de.novatec.showcase.order.ejb.session.InsufficientCreditException;
import de.novatec.showcase.order.ejb.session.OrderSessionLocal;
import de.novatec.showcase.order.mapper.DtoMapper;

@ManagedBean
@Path(value = "/order")
@RolesAllowed({GlobalConstants.ADMIN_ROLE_NAME, GlobalConstants.ORDER_READ_ROLE_NAME})
public class OrderResource {

	@EJB
	private OrderSessionLocal bean;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "{id}")
	public Response getOrder(@PathParam("id") Integer orderId) {
		if (orderId.intValue() <= 0) {
			return Response.serverError().entity("Id cannot be less than 1!").build();
		}
		de.novatec.showcase.order.ejb.entity.Order order = bean.getOrder(orderId);
		if (order == null) {
			return Response.status(Response.Status.NOT_FOUND).entity("Order with id '" + orderId + "' not found!")
					.build();
		}
		return Response.ok().entity(DtoMapper.mapToOrderDto(order)).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "count_bycustomer/{id}")
	public Response countByCustomer(@PathParam("id") Integer customerId) {
		if (customerId <= 0) {
			return Response.serverError().entity("Id cannot be less than 1!").build();
		}
		long count = bean.getOrderCount(customerId);
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("count", count);
		return Response.ok().entity(builder.build()).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "open_orders_by_customer/{id}")
	public Response getOpenOrders(@PathParam("id") Integer customerId) {
		if (customerId <= 0) {
			return Response.serverError().entity("Id cannot be less than 1!").build();
		}
		List<Order> orders = DtoMapper.mapToOrderDto(bean.getOpenOrders(customerId));
		if (orders == null || orders.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND)
					.entity("Orders with customer id '" + customerId + "' not found").build();
		}
		return Response.ok().entity(orders).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path(value = "{customerId}")
	@RolesAllowed({GlobalConstants.ADMIN_ROLE_NAME})
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
		return Response.created(uriInfo.getAbsolutePathBuilder().build()).entity(DtoMapper.mapToOrderDto(bean.getOrder(id))).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "{id}")
	@RolesAllowed({GlobalConstants.ADMIN_ROLE_NAME})
	public Response deleteOrder(@PathParam("id") Integer orderId) {
		// TODO cancel method should return the deleted (or marked as deleted) order id
		// and if the id is not found an exception should be thrown, so that the
		// Response with Response.Status.NOT_FOUND could returned
		bean.cancelOrder(orderId);
		return Response.ok().build();
	}
}