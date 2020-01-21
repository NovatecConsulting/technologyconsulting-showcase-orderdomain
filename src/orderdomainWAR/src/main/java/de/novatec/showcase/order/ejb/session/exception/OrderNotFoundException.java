package de.novatec.showcase.order.ejb.session.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class OrderNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public OrderNotFoundException() {
		super();
	}

	public OrderNotFoundException(String message) {
		super(message);
	}

}
