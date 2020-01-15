package de.novatec.showcase.order.ejb.entity;

import java.io.Serializable;
import java.util.Objects;

public class OrderLinePK implements Serializable {

	private static final long serialVersionUID = 2378709934764446278L;

	private Integer orderId;
	private Integer id;

	public OrderLinePK() {
		super();
	}

	public OrderLinePK(Integer id, Integer orderId) {
		super();
		this.orderId = orderId;
		this.id = id;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public Integer getId() {
		return id;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public void setId(Integer number) {
		this.id = number;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, orderId);
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
		return id == other.id && Objects.equals(orderId, other.orderId);
	}

	@Override
	public String toString() {
		return "OrderLinePK [orderId=" + orderId + ", number=" + id + "]";
	}
}
