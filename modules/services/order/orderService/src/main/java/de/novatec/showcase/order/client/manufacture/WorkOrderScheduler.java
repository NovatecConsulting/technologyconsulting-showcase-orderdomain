package de.novatec.showcase.order.client.manufacture;

import java.util.Calendar;

import javax.annotation.ManagedBean;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import de.novatec.showcase.order.dto.WorkOrder;
import de.novatec.showcase.order.ejb.entity.OrderLine;

@ManagedBean
public class WorkOrderScheduler {

	private static final String JNDI_PROPERTY_MANUFACTUREDOMAIN_WORKORDER_URL = "manufacturedomain.workorder.url";
	private static final String JNDI_PROPERTY_MANUFACTUREDOMAIN_USERNAME = "manufacturedomain.username";
	private static final String JNDI_PROPERTY_MANUFACTUREDOMAIN_PASSWORD = "manufacturedomain.password";
	private static final Logger LOG = LoggerFactory.getLogger(WorkOrderScheduler.class);
	private static final int DEFAULT_LOCATION = 1;
	private String workorderUrl;
	private String username;
	private String password;
	private Client client;

	public WorkOrderScheduler() throws ManufactureDomainNotConfiguredException {
		client = ClientBuilder.newClient();
		client.register(JacksonJsonProvider.class);
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder().build();
		client.register(feature);

		try {
			workorderUrl = (String) new InitialContext().lookup(JNDI_PROPERTY_MANUFACTUREDOMAIN_WORKORDER_URL);
			username = (String) new InitialContext().lookup(JNDI_PROPERTY_MANUFACTUREDOMAIN_USERNAME);
			password = (String) new InitialContext().lookup(JNDI_PROPERTY_MANUFACTUREDOMAIN_PASSWORD);
		} catch (NamingException e) {
			LOG.error("JNDI properties " + JNDI_PROPERTY_MANUFACTUREDOMAIN_WORKORDER_URL + " or "
					+ JNDI_PROPERTY_MANUFACTUREDOMAIN_USERNAME + " or " + JNDI_PROPERTY_MANUFACTUREDOMAIN_PASSWORD
					+ " not found!", e);
			throw new ManufactureDomainNotConfiguredException(
					"One or more JNDI properties for the manufacture domain is/are missing!");
		}
		if (validateJNDIProperty(workorderUrl) || validateJNDIProperty(username) || validateJNDIProperty(password)) {
			throw new ManufactureDomainNotConfiguredException(
					"One or more JNDI properties for the manufacture domain are missing in the server.env file of open liberty!");
		}
	}

	public WorkOrder schedule(OrderLine orderLine) throws RestcallException {
		WorkOrder workOrder = new WorkOrder(DEFAULT_LOCATION, orderLine.getOrderId(), orderLine.getId(),
				orderLine.getQuantity(), Calendar.getInstance(), orderLine.getItem().getId());
		WebTarget target = client.target(workorderUrl);
		Builder builder = target.request(MediaType.APPLICATION_JSON);
		Response response = asAdmin(builder.accept(MediaType.APPLICATION_JSON_TYPE)).post(Entity.json(workOrder));
		if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
			workOrder = response.readEntity(WorkOrder.class);
			return workOrder;
		}
		String message = "Error " + Response.Status.fromStatusCode(response.getStatus()) + " while calling "
				+ workorderUrl + " with " + workOrder;
		if (response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()
				|| response.getStatus() == Response.Status.PRECONDITION_FAILED.getStatusCode()) {
			message = response.readEntity(String.class);
		}
		throw new RestcallException(message);
	}

	private Builder asAdmin(Builder builder) {
		return asUser(builder, username, password);
	}

	private static Builder asUser(Builder builder, String userName, String password) {
		return builder.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_USERNAME, userName)
				.property(HttpAuthenticationFeature.HTTP_AUTHENTICATION_BASIC_PASSWORD, password);
	}

	private boolean validateJNDIProperty(String value) {
		return value.startsWith("${env.");
	}
}