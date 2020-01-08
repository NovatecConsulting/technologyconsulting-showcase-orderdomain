package de.novatec.showcase.order.ejb.session.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class PriceException extends Exception {

	private static final long serialVersionUID = -2605722900558101557L;

	public PriceException() {
		super();
	}

	public PriceException(String message) {
		super(message);
	}

}
