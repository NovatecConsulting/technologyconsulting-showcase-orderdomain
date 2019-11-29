package de.novatec.showcase.controller;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
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

import de.novatec.showcase.GlobalConstants;
import de.novatec.showcase.dto.Customer;
import de.novatec.showcase.dto.CustomerInventory;
import de.novatec.showcase.ejb.orders.session.CustomerSessionLocal;
import de.novatec.showcase.mapper.DtoMapper;

@ManagedBean
@Path(value = "/customer")
@RolesAllowed({GlobalConstants.ADMIN_ROLE_NAME, GlobalConstants.CUSTOMER_READ_ROLE_NAME})
public class CustomerResource {

	@EJB
	private CustomerSessionLocal bean;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "{id}")
	public Response getCustomer(@PathParam("id") Integer customerId) {
		if (customerId <= 0) {
			return Response.serverError().entity("Id cannot be less than 1!").build();
		}
		Customer customer = DtoMapper.mapToCustomerDto(bean.getCustomer(customerId));
		if (customer == null) {
			return Response.status(Response.Status.NOT_FOUND).entity("Customer with id '" + customerId + "' not found!")
					.build();
		}
		return Response.ok().entity(customer).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "count")
	public Response countCustomer() {
		long count = bean.countCustomer();
        JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("count", count);
		return Response.ok().entity(builder.build()).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "exist/{id}")
	public Response customerIdExist(@PathParam("id") Integer customerId) {
		if (customerId <= 0) {
			return Response.serverError().entity("Id cannot be less than 1!").build();
		}
		boolean exist = bean.validateCustomer(customerId);
        JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("exist", exist);
		return Response.ok().entity(builder.build()).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "inventories/{id}")
	public Response getInventories(@PathParam("id") Integer customerId) {
		if (customerId <= 0) {
			return Response.serverError().entity("Id cannot be less than 1!").build();
		}
		List<CustomerInventory> inventories = DtoMapper.mapToCustomerInventoryDto(bean.getInventories(customerId));
		if (inventories == null || inventories.isEmpty()) {
			return Response.status(Response.Status.NOT_FOUND)
					.entity("Customer with id '" + customerId + "' has no inventory!").build();
		}
		return Response.ok().entity(inventories).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "with_good_credit")
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
	public Response checkCustomerCredit(@PathParam("id") Integer customerId, @PathParam("costs") BigDecimal costs) {
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
	public Response addInventory(@PathParam("id") Integer orderId) {
		// TODO addInventory method should return the changed customer id
		// and if the id is not found an exception should be thrown, so that the
		// Response with Response.Status.NOT_FOUND could returned
		bean.addInventory(orderId);
		return Response.ok().build();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "sell_inventory/{customerId}/{itemId}/{quantity}")
	public Response sellInventory(@PathParam("customerId") Integer customerId, @PathParam("itemId") String itemId,
			@PathParam("quantity") int quantity) {
		// TODO if the one of the ids is not found an exception should be thrown, so
		// that the Response with Response.Status.NOT_FOUND could returned
		boolean sold = bean.sellInventory(customerId, itemId, quantity);
        JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("sold", sold);
		return Response.ok().entity(builder.build()).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@RolesAllowed({GlobalConstants.ADMIN_ROLE_NAME})
	public Response createCustomer(Customer customer, @Context UriInfo uriInfo) {
		// TODO validate customer
		customer.setSince(Calendar.getInstance());
		Integer id = bean.createCustomer(DtoMapper.mapToCustomerEntity(customer));
        JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("id", id);
		return Response.created(uriInfo.getAbsolutePathBuilder().build()).entity(builder.build()).build();
	}
}
