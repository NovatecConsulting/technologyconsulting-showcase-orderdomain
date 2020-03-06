package de.novatec.showcase.order.controller;

import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.ManagedBean;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.novatec.showcase.order.kafka.KafkaConfiguration;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import de.novatec.showcase.order.GlobalConstants;
import de.novatec.showcase.order.client.manufacture.RestcallException;
import de.novatec.showcase.order.dto.ItemQuantityPair;
import de.novatec.showcase.order.dto.ItemQuantityPairs;
import de.novatec.showcase.order.dto.Order;
import de.novatec.showcase.order.dto.ShoppingCart;
import de.novatec.showcase.order.ejb.session.OrderSessionLocal;
import de.novatec.showcase.order.ejb.session.exception.CustomerNotFoundException;
import de.novatec.showcase.order.ejb.session.exception.InsufficientCreditException;
import de.novatec.showcase.order.ejb.session.exception.ItemNotFoundException;
import de.novatec.showcase.order.ejb.session.exception.OrderNotFoundException;
import de.novatec.showcase.order.ejb.session.exception.PriceException;
import de.novatec.showcase.order.ejb.session.exception.SpecificationException;
import de.novatec.showcase.order.mapper.DtoMapper;

@ManagedBean
@Path(value = "/order")
@RolesAllowed({GlobalConstants.ADMIN_ROLE_NAME, GlobalConstants.ORDER_READ_ROLE_NAME})
@Tags(value= {@Tag(name = "Order")})
public class OrderResource {

	@EJB
	private OrderSessionLocal bean;

	@Inject
	private Producer<Integer, JsonNode> producer;

	private static final ObjectMapper mapper = new ObjectMapper();


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "{id}")
	@APIResponses(
	        value = {
	            @APIResponse(
	                responseCode = "404",
	                description = "Order not found",
	                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
	            @APIResponse(
	            		responseCode = "400",
	            		description = "Order id is less than 1",
	            		content = @Content(mediaType = MediaType.TEXT_PLAIN)),
	            @APIResponse(
	                responseCode = "200",
	                description = "The order with the given id.",
	                content = @Content(mediaType = MediaType.APPLICATION_JSON,
	                schema = @Schema(implementation = Order.class))) })
	    @Operation(
	        summary = "Get the order by id",
	        description = "Get the order by id where the id has to be higher than 0.")
	public Response getOrder(
			@Parameter(
            description = "The id of the order which should be retrieved.",
            required = true,
            example = "1",
            schema = @Schema(type = SchemaType.INTEGER)) 
			@PathParam("id") Integer orderId) {
		if (orderId.intValue() <= 0) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Id cannot be less than 1!").type(MediaType.TEXT_PLAIN_TYPE).build();
		}
		de.novatec.showcase.order.ejb.entity.Order order;
		try {
			order = bean.getOrder(orderId);
		} catch (OrderNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND)
					.entity(e.getMessage()).type(MediaType.TEXT_PLAIN_TYPE).build();
		}
		return Response.ok().entity(DtoMapper.mapToOrderDto(order)).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "count_bycustomer/{id}")
	@APIResponses(
	        value = {
		       @APIResponse(
		    		responseCode = "400",
		            description = "Customer id is less than 1",
		            content = @Content(mediaType = MediaType.TEXT_PLAIN)),
	            @APIResponse(
	                responseCode = "200",
	                description = "The order with the given id.",
	                content = @Content(mediaType = MediaType.APPLICATION_JSON)) })
	    @Operation(
	        summary = "Count the orders by the customer id",
	        description = "Count the order by customer id where the id has to be higher than 0.")
	public Response countByCustomer(
			@Parameter(
		            description = "The id of the customer where to count the order from.",
		            required = true,
		            example = "1",
		            schema = @Schema(type = SchemaType.INTEGER)) 
			@PathParam("id") Integer customerId) {
		if (customerId <= 0) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Id cannot be less than 1!").type(MediaType.TEXT_PLAIN_TYPE).build();
		}
		long count = bean.getOrderCount(customerId);
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("count", count);
		return Response.ok().entity(builder.build()).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "open_orders_by_customer/{id}")
	@APIResponses(
	        value = {
	 		       @APIResponse(
	 			    		responseCode = "400",
	 			            description = "Customer id is less than 1",
	 			            content = @Content(mediaType = MediaType.TEXT_PLAIN)),
	            @APIResponse(
	                responseCode = "404",
	                description = "No Orders with the given customer id are found",
	                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
	            @APIResponse(
	                responseCode = "200",
	                description = "The open for the given customer id.",
	                content = @Content(mediaType = MediaType.APPLICATION_JSON,
	                schema = @Schema(implementation = Order.class))) })
	    @Operation(
	        summary = "Get the open orders by customer id",
	        description = "Get the open orders by customer id where the id has to be higher than 0.")
	public Response getOpenOrders(
			@Parameter(
		            description = "The id of the customer where to get the open orders for.",
		            required = true,
		            example = "1",
		            schema = @Schema(type = SchemaType.INTEGER)) 
			@PathParam("id") Integer customerId) {
		if (customerId <= 0) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Id cannot be less than 1!").type(MediaType.TEXT_PLAIN_TYPE).build();
		}
		List<Order> orders = DtoMapper.mapToOrderDto(bean.getOpenOrders(customerId));
		if (orders == null || orders.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND)
					.entity("No open orders with customer id '" + customerId + "' not found").type(MediaType.TEXT_PLAIN_TYPE).build();
		}
		return Response.ok().entity(orders).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path(value = "{customerId}")
	@Asynchronous
	@RolesAllowed({GlobalConstants.ADMIN_ROLE_NAME})
	@APIResponses(
	        value = {
	 		   @APIResponse(
	 				responseCode = "500",
	 			    description = "The REST call to manufature schedule workorder failed for a large order,",
	 			    content = @Content(mediaType = MediaType.TEXT_PLAIN)),
	            @APIResponse(
	                responseCode = "404",
	                description = "Customer with the given id not found",
	                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
	            @APIResponse(
	            		responseCode = "400",
	            		description = "Customer id is less than 1",
	            		content = @Content(mediaType = MediaType.TEXT_PLAIN)),
	            @APIResponse(
	            		responseCode = "412",
	            		description = "One of the preconditions failed",
	            		content = @Content(mediaType = MediaType.TEXT_PLAIN)),
	            @APIResponse(
	                responseCode = "201",
	                description = "The new order for the given customer id.",
	                content = @Content(mediaType = MediaType.APPLICATION_JSON,
	                schema = @Schema(implementation = Order.class))) })
	@RequestBody(
        name="itemQuantityPairs",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = ItemQuantityPairs.class)),
        required = true,
        description = "example of a list with items and quantity"
    )
	@Operation(
			summary = "Create an new order",
			description = "Create an new order for a given customer id and a List of items/quantities.")
	public void createOrder(
			@Parameter(
		            description = "The id of the customer where to create a new order for.",
		            required = true,
		            example = "1",
		            schema = @Schema(type = SchemaType.INTEGER)) 
			@PathParam("customerId") Integer customerId,
			@Valid ItemQuantityPairs itemQuantityPairs,
			@Context UriInfo uriInfo,
			@Suspended final AsyncResponse asyncResponse) {
		if (customerId.intValue() <= 0) {
			asyncResponse.resume(Response.status(Response.Status.BAD_REQUEST).entity("Customer id cannot be less than 1!").type(MediaType.TEXT_PLAIN_TYPE).build());
		}

		ShoppingCart shoppingCart = new ShoppingCart();
		for (ItemQuantityPair itemQuantityPair : itemQuantityPairs.getItemQuantityPairs()) {
			shoppingCart.addItem(itemQuantityPair.getItem(), itemQuantityPair.getQuantity());
		}
		de.novatec.showcase.order.ejb.entity.Order order;
		try {
			order = bean.newOrder(customerId, shoppingCart);
			Order order_dto=DtoMapper.mapToOrderDto(order);
			ProducerRecord<Integer,JsonNode> record = new ProducerRecord<Integer, JsonNode>(KafkaConfiguration.TOPIC_NAME, order_dto.getId(), mapper.valueToTree(order_dto));
			Future<RecordMetadata> kafkaStatus = producer.send(record);
			kafkaStatus.get();
			asyncResponse.resume(Response.created(uriInfo.getAbsolutePathBuilder().build()).entity(DtoMapper.mapToOrderDto(order)).type(MediaType.APPLICATION_JSON_TYPE).build());
		} catch (CustomerNotFoundException e) {
			asyncResponse.resume(Response.status(Response.Status.NOT_FOUND)
					.entity(e.getMessage()).type(MediaType.TEXT_PLAIN_TYPE).build());
		} catch (ItemNotFoundException e) {
			asyncResponse.resume(Response.status(Response.Status.NOT_FOUND)
					.entity(e.getMessage()).type(MediaType.TEXT_PLAIN_TYPE).build());
		} catch (InsufficientCreditException e) {
			asyncResponse.resume(Response.status(Response.Status.PRECONDITION_FAILED)
					.entity("The customer with id '" + customerId + "' has insufficient credit!").type(MediaType.TEXT_PLAIN_TYPE).build());
		} catch (PriceException e) {
			asyncResponse.resume(Response.status(Response.Status.PRECONDITION_FAILED)
					.entity(e.getMessage()).type(MediaType.TEXT_PLAIN_TYPE).build());
		} catch (SpecificationException e) {
			asyncResponse.resume(Response.status(Response.Status.PRECONDITION_FAILED)
					.entity(e.getMessage()).type(MediaType.TEXT_PLAIN_TYPE).build());
		} catch (RestcallException e) {
			asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(e.getMessage()).type(MediaType.TEXT_PLAIN_TYPE).build());
		} catch (Exception e) {
		asyncResponse.resume(Response.status(Response.Status.SERVICE_UNAVAILABLE)
				.entity(e.getMessage()).type(MediaType.TEXT_PLAIN_TYPE).build());
		}

	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "{id}")
	@RolesAllowed({GlobalConstants.ADMIN_ROLE_NAME})
	@APIResponses(
	        value = {
 		       @APIResponse(
 			    		responseCode = "400",
 			            description = "Order id is less than 1",
 			            content = @Content(mediaType = MediaType.TEXT_PLAIN)),
	            @APIResponse(
		                responseCode = "404",
		                description = "Order with the given id not found",
		                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
	            @APIResponse(
	                responseCode = "200",
	                description = "The order with the given id was deleted if found.",
	                content = @Content(mediaType = MediaType.APPLICATION_JSON,
	                schema = @Schema(implementation = Order.class))) })
	    @Operation(
	        summary = "Delete order by id",
	        description = "Delete the order with the given id if it is found.")
	public Response deleteOrder(
			@Parameter(
		            description = "The id of the order which should be deleted.",
		            required = true,
		            example = "1",
		            schema = @Schema(type = SchemaType.INTEGER)) 
			@PathParam("id") Integer orderId) {
		if (orderId.intValue() <= 0) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Order id cannot be less than 1!").type(MediaType.TEXT_PLAIN_TYPE).build();
		}
		try {
			return Response.ok().entity(DtoMapper.mapToOrderDto(bean.cancelOrder(orderId))).type(MediaType.APPLICATION_JSON_TYPE).build();
		} catch (OrderNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND)
					.entity(e.getMessage()).type(MediaType.TEXT_PLAIN_TYPE).build();
		}
	}
}