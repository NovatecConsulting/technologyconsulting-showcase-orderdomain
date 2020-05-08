package de.novatec.showcase.order.client.manufacture;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ManufactureDomainNotConfiguredException extends Exception {

	private static final long serialVersionUID = -2605722900558101557L;

	public ManufactureDomainNotConfiguredException() {
		super();
	}

	public ManufactureDomainNotConfiguredException(String message) {
		super(message);
	}

}
