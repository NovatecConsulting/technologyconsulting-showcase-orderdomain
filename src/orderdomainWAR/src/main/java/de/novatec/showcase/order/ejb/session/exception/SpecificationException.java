package de.novatec.showcase.order.ejb.session.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class SpecificationException extends Exception {

	private static final long serialVersionUID = -2605722900558101557L;

	public SpecificationException() {
		super();
	}

	public SpecificationException(String message) {
		super(message);
	}

}
