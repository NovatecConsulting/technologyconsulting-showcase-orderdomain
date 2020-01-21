package de.novatec.showcase.order.ejb.session.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class CustomerNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public CustomerNotFoundException() {
		super();
	}

	public CustomerNotFoundException(String message) {
		super(message);
	}

}
