package de.novatec.showcase.ejb.orders.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;

@Embeddable
public class OrderLinePK implements Serializable {

	private static final long serialVersionUID = 2378709934764446278L;

	private Integer orderId;
	private int number;

	public OrderLinePK() {
		super();
	}

	public OrderLinePK(Integer orderID, int number) {
		this.orderId = orderID;
		this.number = number;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public int getNumber() {
		return number;
	}

	@Override
	public int hashCode() {
		return Objects.hash(number, orderId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof OrderLinePK)) {
			return false;
		}
		OrderLinePK other = (OrderLinePK) obj;
		return number == other.number && Objects.equals(orderId, other.orderId);
	}

	@Override
	public String toString() {
		return "OrderLinePK [orderId=" + orderId + ", number=" + number + "]";
	}
}
