package de.novatec.showcase.ejb.orders.session;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class InsufficientCreditException extends Exception {

	private static final long serialVersionUID = -2605722900558101557L;

	public InsufficientCreditException() {
		super();
	}

	public InsufficientCreditException(String arg0) {
		super(arg0);
	}

}
