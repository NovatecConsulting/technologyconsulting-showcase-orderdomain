package de.novatec.showcase.order.client.manufacture;

import java.util.Calendar;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import de.novatec.showcase.order.dto.WorkOrder;
import de.novatec.showcase.order.ejb.entity.OrderLine;

public class WorkOrderScheduler {

	private static final int DEFAULT_LOCATION = 1;
	private static final String USERNAME = System.getProperty("username.manufacture");
	private static final String PASSWORD = System.getProperty("password.manufacture");
	private static final String PORT = System.getProperty("http.port.manufacture");
	private static final String BASE_URL = "http://localhost:" + PORT + "/manufacturedomain/";

	private static final String WORKORDER_URL = BASE_URL + "workorder/";
	private Client client;

	public WorkOrderScheduler() {
		client = ClientBuilder.newClient();
		client.register(JacksonJsonProvider.class);
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder().build();
		client.register(feature);
	}

	public WorkOrder schedule(OrderLine orderLine) throws RestcallException {
		WorkOrder workOrder = new WorkOrder(DEFAULT_LOCATION, orderLine.getPk().getOrderId(),
				orderLine.getPk().getNumber(), orderLine.getQuantity(), Calendar.getInstance(),
				orderLine.getItem().getId());
		WebTarget target = client.target(WORKORDER_URL);
		Builder builder = target.request(MediaType.APPLICATION_JSON);
		Response response = asAdmin(builder.accept(MediaType.APPLICATION_JSON_TYPE)).post(Entity.json(workOrder));
		if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
			workOrder = response.readEntity(WorkOrder.class);
			return workOrder;
		}
		String message = "Error " + Response.Status.fromStatusCode(response.getStatus()) + " while calling " + WORKORDER_URL + " with " + workOrder;
		if (response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
		{
			message = response.readEntity(String.class);
		}
		throw new RestcallException(message);
	}

	private static Builder asAdmin(Builder builder) {
		return asUser(builder, USERNAME, PASSWORD);
	}

	private static Builder asUser(Builder builder, String userName, String password) {
		return builder.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME, userName)
				.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD, password);
	}
}