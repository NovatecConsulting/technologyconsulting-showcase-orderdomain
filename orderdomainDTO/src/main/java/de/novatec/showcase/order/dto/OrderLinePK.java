package de.novatec.showcase.order.dto;

import java.io.Serializable;
import java.util.Objects;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="OrderLinePK", description="POJO that represents a order line priomary key.")
public class OrderLinePK implements Serializable {

	private static final long serialVersionUID = 1L;
	@Schema(required = true)
	private Integer orderId;
	@Schema(required = true)
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

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public void setNumber(int number) {
		this.number = number;
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
