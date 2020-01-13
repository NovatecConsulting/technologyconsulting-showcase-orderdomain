package de.novatec.showcase.order.controller;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObject;
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

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;

import de.novatec.showcase.order.GlobalConstants;
import de.novatec.showcase.order.dto.Item;
import de.novatec.showcase.order.ejb.session.ItemSessionLocal;
import de.novatec.showcase.order.mapper.DtoMapper;

@ManagedBean
@Path(value = "/item")
@RolesAllowed({GlobalConstants.ADMIN_ROLE_NAME, GlobalConstants.ITEM_READ_ROLE_NAME})
@Tags(value= {@Tag(name = "Item")})
public class ItemResource {


	@EJB
	private ItemSessionLocal itemBean;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@RolesAllowed({GlobalConstants.ADMIN_ROLE_NAME})
	@APIResponses(
	        value = {
	            @APIResponse(
	                responseCode = "201",
	                description = "The new item.",
	                content = @Content(mediaType = MediaType.APPLICATION_JSON,
	                schema = @Schema(implementation = Item.class))) })
	@RequestBody(
            name="item",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Item.class)),
            required = true,
            description = "example of a item"
        )
	@Operation(
			summary = "Create an new item",
			description = "Create an new item with the given item.")
	public Response createItem(Item item, @Context UriInfo uriInfo) {
		String id = itemBean.createItem(DtoMapper.mapToItemEntity(item));
		return Response.created(uriInfo.getAbsolutePathBuilder().build()).entity(DtoMapper.mapToItemDto(itemBean.getItems(id)).get(0)).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "count")
	@APIResponses(
	        value = {
	            @APIResponse(
	                responseCode = "200",
	                description = "The number of items.",
	                content = @Content(mediaType = MediaType.APPLICATION_JSON,
	                schema = @Schema(implementation = JsonObject.class ))) })
	@Operation(
			summary = "Count the number of items",
			description = "Count the number of items in the database.")
	public Response countItem() {
		long count = itemBean.getTotalItems();
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("count", count);
		return Response.ok().entity(builder.build()).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "batch_size")
	@Operation(deprecated = true, summary = "not used right now!")
	public Response getBatchSize() {
		long batchSize = itemBean.getBatchSize();
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("batchSize", batchSize);
		return Response.ok().entity(builder.build()).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "current_max")
	@Operation(deprecated = true, summary = "not used right now!")
	public Response getCurrentMax() {
		String current_max = itemBean.getCurrentMax();
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("current_max", current_max);
		return Response.ok().entity(builder.build()).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "current_min")
	@Operation(deprecated = true, summary = "not used right now!")
	public Response getCurrentMin() {
		String current_min = itemBean.getCurrentMin();
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("current_min", current_min);
		return Response.ok().entity(builder.build()).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "{ids}")
	@APIResponses(
	        value = {
	            @APIResponse(
	                responseCode = "200",
	                description = "The items with the given ids.",
	                content = @Content(mediaType = MediaType.APPLICATION_JSON,
	                schema = @Schema(type= SchemaType.ARRAY, implementation = Item.class))) })
	@Operation(
			summary = "Get items by ids.",
			description = "Get the list of items by the given comma seperated list of ids.")
	public Response getItems(@PathParam("ids") String itemIds) {		
		List<Item> items = DtoMapper.mapToItemDto(itemBean.getItems(itemIds));
		return Response.ok().entity(items).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "forward")
	@Operation(deprecated = true, summary = "not used right now!")
	public Response forward() {
		List<Item> items = DtoMapper.mapToItemDto(itemBean.browseForward());
		return Response.ok().entity(items).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "reverse")
	@Operation(deprecated = true, summary = "not used right now!")
	public Response reverse() {
		List<Item> items = DtoMapper.mapToItemDto(itemBean.browseReverse());
		return Response.ok().entity(items).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path(value = "batch_size/{size}")
	@Operation(deprecated = true, summary = "not used right now!")
	public Response setBatchSize(@PathParam("size") int batchSize) {
		itemBean.setBatchSize(batchSize);
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("batchSize", itemBean.getBatchSize());
		return Response.ok().entity(builder.build()).type(MediaType.APPLICATION_JSON_TYPE).build();
	}
}
