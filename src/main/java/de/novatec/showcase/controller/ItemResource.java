package de.novatec.showcase.controller;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
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

import de.novatec.showcase.ejb.orders.entity.Item;
import de.novatec.showcase.ejb.orders.session.ItemSessionLocal;

@ManagedBean
@Path(value = "/item")
public class ItemResource {

	@EJB
	private ItemSessionLocal itemBean;

	private JsonHelper jsonHelper = new JsonHelper();

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createItem(Item item, @Context UriInfo uriInfo) {
		String id = itemBean.createItem(item);
		String json = "{ \"id\": \"" + id + "\" }";
		return Response.created(uriInfo.getAbsolutePathBuilder().build()).entity(json).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "count")
	public Response countItem() {
		long count = itemBean.getTotalItems();
		String json = "{ \"count\": \"" + count + "\" }";
		return Response.ok(json).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "batch_size")
	public Response getBatchSize() {
		long batchSize = itemBean.getBatchSize();
		String json = "{ \"batchSize\": \"" + batchSize + "\" }";
		return Response.ok(json).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "current_max")
	public Response getCurrentMax() {
		String current_max = itemBean.getCurrentMax();
		String json = "{ \"current_max\": \"" + current_max + "\" }";
		return Response.ok(json).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "current_min")
	public Response getCurrentMin() {
		String current_min = itemBean.getCurrentMin();
		String json = "{ \"current_min\": \"" + current_min + "\" }";
		return Response.ok(json).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "{ids}")
	public Response getItems(@PathParam("ids") String itemIds) {
		List<Item> items = itemBean.getItems(itemIds);
		String json = jsonHelper.toJson(items);
		return Response.ok(json).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "forward")
	public Response forward() {
		List<Item> items = itemBean.browseForward();
		String json = jsonHelper.toJson(items);
		return Response.ok(json).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "reverse")
	public Response reverse() {
		List<Item> items = itemBean.browseReverse();
		String json = jsonHelper.toJson(items);
		return Response.ok(json).build();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "batch_size/{size}")
	public Response setBatchSize(@PathParam("size") int batchSize) {
		itemBean.setBatchSize(batchSize);
		String json = "{ \"batchSize\": \"" + itemBean.getBatchSize() + "\" }";
		return Response.ok(json).build();
	}
}
