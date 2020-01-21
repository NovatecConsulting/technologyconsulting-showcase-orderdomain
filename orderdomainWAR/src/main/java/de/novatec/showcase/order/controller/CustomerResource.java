package de.novatec.showcase.order.controller;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

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
import de.novatec.showcase.order.dto.Customer;
import de.novatec.showcase.order.dto.CustomerInventory;
import de.novatec.showcase.order.dto.Order;
import de.novatec.showcase.order.ejb.session.CustomerSessionLocal;
import de.novatec.showcase.order.ejb.session.exception.CustomerNotFoundException;
import de.novatec.showcase.order.ejb.session.exception.ItemNotFoundException;
import de.novatec.showcase.order.ejb.session.exception.OrderNotFoundException;
import de.novatec.showcase.order.mapper.DtoMapper;

@ManagedBean
@Path(value = "/customer")
@RolesAllowed({GlobalConstants.ADMIN_ROLE_NAME, GlobalConstants.CUSTOMER_READ_ROLE_NAME})
@Tags(value= {@Tag(name = "Customer")})
public class CustomerResource {

	@EJB
	private CustomerSessionLocal bean;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "{id}")
	@APIResponses(
	        value = {
	            @APIResponse(
	                responseCode = "404",
	                description = "Customer not found",
	                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
	            @APIResponse(
	            		responseCode = "500",
	            		description = "Customer id is less than 1",
	            		content = @Content(mediaType = MediaType.TEXT_PLAIN)),
	            @APIResponse(
	                responseCode = "200",
	                description = "The customer with the given id.",
	                content = @Content(mediaType = MediaType.APPLICATION_JSON,
	                schema = @Schema(implementation = Order.class))) })
	    @Operation(
	        summary = "Get the customer by id",
	        description = "Get the customer by id where the id has to be higher than 0.")
	public Response getCustomer(
			@Parameter(
		            description = "The id of the customer which should be retrieved.",
		            required = true,
		            example = "1",
		            schema = @Schema(type = SchemaType.INTEGER)) 
			@PathParam("id") Integer customerId) {
		if (customerId <= 0) {
			return Response.serverError().entity("Id cannot be less than 1!").build();
		}
		de.novatec.showcase.order.ejb.entity.Customer customer;
		try {
			customer = bean.getCustomer(customerId);
		} catch (CustomerNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).entity("Customer with id '" + customerId + "' not found!")
					.build();
		}
		return Response.ok().entity(DtoMapper.mapToCustomerDto(customer)).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "count")
	@APIResponses(
	        value = {
	            @APIResponse(
	                responseCode = "200",
	                description = "The number of customers.",
	                content = @Content(mediaType = MediaType.APPLICATION_JSON,
	                schema = @Schema(implementation = JsonObject.class ))) })
	@Operation(
			summary = "Count the number of customer",
			description = "Count the number of customers in the database.")
	public Response countCustomer() {
		long count = bean.countCustomer();
        JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("count", count);
		return Response.ok().entity(builder.build()).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "inventories/{id}")
	@APIResponses(
	        value = {
		        @APIResponse(
		        	responseCode = "404",
			        description = "Customer not found",
			        content = @Content(mediaType = MediaType.TEXT_PLAIN)),
			    @APIResponse(
			        responseCode = "500",
			        description = "Customer id is less than 1",
			        content = @Content(mediaType = MediaType.TEXT_PLAIN)),
	            @APIResponse(
	                responseCode = "200",
	                description = "The customer inventories for a given customer id.",
	                content = @Content(mediaType = MediaType.APPLICATION_JSON,
	                schema = @Schema(type= SchemaType.ARRAY, implementation = CustomerInventory.class ))) })
	@Operation(
			summary = "Get the customer inventories",
			description = "Get the customer inventories for a given customer id.")
	public Response getInventories(
			@Parameter(
		            description = "The id of the customer where to where to get the customer inventories for.",
		            required = true,
		            example = "1",
		            schema = @Schema(type = SchemaType.INTEGER)) 
			@PathParam("id") Integer customerId) {
		if (customerId <= 0) {
			return Response.serverError().entity("Id cannot be less than 1!").build();
		}
		List<CustomerInventory> inventories;
		try {
			inventories = DtoMapper.mapToCustomerInventoryDto(bean.getInventories(customerId));
		} catch (CustomerNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND)
					.entity(e.getMessage()).type(MediaType.TEXT_PLAIN_TYPE).build();
		}
		if (inventories == null || inventories.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND)
					.entity("Customer with id '" + customerId + "' has no inventory!").build();
		}
		return Response.ok().entity(inventories).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "with_good_credit")
	@APIResponses(
	        value = {
		        @APIResponse(
			        responseCode = "404",
			        description = "No customer with good credit found",
			        content = @Content(mediaType = MediaType.TEXT_PLAIN)),
	            @APIResponse(
	                responseCode = "200",
	                description = "The customers with good credit.",
	                content = @Content(mediaType = MediaType.APPLICATION_JSON,
	                schema = @Schema(type= SchemaType.ARRAY, implementation = Customer.class))) })
	@Operation(
			summary = "Get the customer with good credit",
			description = "Get the customer with good credit.")
	public Response customersWithGoodCredit() {
		List<Customer> customers = DtoMapper.mapToCustomerDto(bean.selectCustomerWithGoodCredit());
		if (customers == null || customers.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND).entity("No Customer with good credit found!").build();
		}
		return Response.ok().entity(customers).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "check_credit/{id}/{costs}")
	@APIResponses(
	        value = {
				@APIResponse(
					responseCode = "500",
					description = "Customer id is less than 1",
					content = @Content(mediaType = MediaType.TEXT_PLAIN)),
	            @APIResponse(
	                responseCode = "200",
	                description = "Boolean Value if the customer has credit.",
	                content = @Content(mediaType = MediaType.APPLICATION_JSON,
	                schema = @Schema(implementation = Customer.class))) })
	@Operation(
			summary = "Check if the customer has credit",
			description = "Check if the customer with the given id has credit.")
	public Response checkCustomerCredit(
			@Parameter(
		            description = "The id of the customer where to where to check the customer credit for.",
		            required = true,
		            example = "1",
		            schema = @Schema(type = SchemaType.INTEGER)) 
			@PathParam("id") Integer customerId, 
			@Parameter(
		            description = "The cost.",
		            required = true,
		            example = "100",
		            schema = @Schema(type = SchemaType.NUMBER)) 
			@PathParam("costs") BigDecimal costs) {
		if (customerId <= 0) {
			return Response.serverError().entity("Id cannot be less than 1!").build();
		}
		boolean hasCredit = bean.checkCustomerCredit(customerId, costs);
        JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("credit", hasCredit);
		return Response.ok().entity(builder.build()).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "add_inventory/{id}")
	@APIResponses(
	        value = {
		        @APIResponse(
			        responseCode = "404",
			        description = "No order with the id is not found",
					content = @Content(mediaType = MediaType.TEXT_PLAIN)),
				@APIResponse(
	                responseCode = "200",
	                description = "The inventory has been added.") })
	@Operation(
			summary = "Add inventory",
			description = "Add inventory to customer of the given oder id.")
	public Response addInventory(
			@Parameter(
		            description = "The order id where to add the inventory for.",
		            required = true,
		            example = "1",
		            schema = @Schema(type = SchemaType.INTEGER)) 
			@PathParam("id") Integer orderId) {
		try {
			bean.addInventory(orderId);
		} catch (OrderNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND)
					.entity(e.getMessage()).type(MediaType.TEXT_PLAIN_TYPE).build();
		}
		return Response.ok().build();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "sell_inventory/{customerId}/{itemId}/{quantity}")
	@APIResponses(
	        value = {
	            @APIResponse(
		            responseCode = "404",
		            description = "The Customer/Item with the given id is notfound.",
		            content = @Content(mediaType = MediaType.TEXT_PLAIN)),
				@APIResponse(
	                responseCode = "200",
	                description = "The quantity of items has been sold for a customer inventory.") })
	@Operation(
			summary = "Sell inventory",
			description = "Sell the quantity of items for a customer inventory.")
	public Response sellInventory(
			@Parameter(
		            description = "The customer id where to sell items.",
		            required = true,
		            example = "1",
		            schema = @Schema(type = SchemaType.INTEGER)) 
			@PathParam("customerId") Integer customerId, 
			@Parameter(
		            description = "The item id which has to be sold.",
		            required = true,
		            example = "1",
		            schema = @Schema(type = SchemaType.STRING)) 
			@PathParam("itemId") String itemId,
			@Parameter(
		            description = "The quantity of the item which has to be sold.",
		            required = true,
		            example = "1",
		            schema = @Schema(type = SchemaType.INTEGER)) 
			@PathParam("quantity") int quantity) {
		boolean sold = false;
		try {
			sold = bean.sellInventory(customerId, itemId, quantity);
		} catch (ItemNotFoundException | CustomerNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND)
					.entity(e.getMessage()).type(MediaType.TEXT_PLAIN_TYPE).build();
		}
        JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("sold", sold);
		return Response.ok().entity(builder.build()).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@RolesAllowed({GlobalConstants.ADMIN_ROLE_NAME})
	@APIResponses(
	        value = {
	            @APIResponse(
	                responseCode = "400",
	                description = "If the given Customer is not valid.",
	                content = @Content(mediaType = MediaType.TEXT_PLAIN)),
	            @APIResponse(
	            	responseCode = "201",
	            	description = "The new item.",
	            	content = @Content(mediaType = MediaType.APPLICATION_JSON,
	            	schema = @Schema(implementation = Customer.class))) })
	@RequestBody(
            name="customer",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Customer.class)),
            required = true,
            description = "example of a customer"
        )
	@Operation(
			summary = "Create an new customer",
			description = "Create an new customer with the given customer.")
	public Response createCustomer(@Valid Customer customer, @Context UriInfo uriInfo) {
		customer.setSince(Calendar.getInstance());
		return Response.created(uriInfo.getAbsolutePathBuilder().build())
				.entity(DtoMapper.mapToCustomerDto(bean.createCustomer(DtoMapper.mapToCustomerEntity(customer))))
				.build();
	}
}
