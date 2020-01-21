package de.novatec.showcase.order.controller;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

	@Override
	public Response toResponse(final ConstraintViolationException exception) {
		return Response.status(Response.Status.BAD_REQUEST).entity(prepareMessage(exception)).type(MediaType.TEXT_PLAIN)
				.build();
	}

	private String prepareMessage(ConstraintViolationException exception) {
		StringBuilder message = new StringBuilder();
		for (ConstraintViolation<?> contrainViolation : exception.getConstraintViolations()) {
			message.append(contrainViolation.getLeafBean());
			message.append(" ");
			message.append(contrainViolation.getInvalidValue());
			message.append(" ");
			message.append(contrainViolation.getMessage());
			message.append(System.lineSeparator());
		}
		return message.toString();
	}
}
