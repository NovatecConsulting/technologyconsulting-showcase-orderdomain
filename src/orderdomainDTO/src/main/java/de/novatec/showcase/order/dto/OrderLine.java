package de.novatec.showcase.order.dto;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Objects;

import javax.json.bind.annotation.JsonbTransient;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Schema(name="OrderLine", description="POJO that represents a order line.")
public class OrderLine {
	
	private Integer id;
	
	private Integer orderId;

	private int quantity;

	private Calendar shipDate;

	private OrderStatus status;

	private BigDecimal totalValue;

	private BigDecimal msrpAtPurchase;

	@JsonbTransient
	@JsonIgnore
	private Order order;

	private Item item;

	private Integer version;

	public OrderLine() {
		super();
	}

	public OrderLine( int quantity, BigDecimal totalValue, BigDecimal msrpAtPurchase,
			Order order, Item item) {
		super();
		this.orderId = order.getId();
		this.quantity = quantity;
		this.status = OrderStatus.DEFERRED;
		this.totalValue = totalValue;
		this.msrpAtPurchase = msrpAtPurchase;
		this.order = order;
		this.item = item;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public Order getOrder() {
		return order;
	}
	
	public void setOrder(Order order) {
		this.order = order;
	}

	public Item getItem() {
		return item;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Calendar getShipDate() {
		return shipDate;
	}

	public void setShipDate(Calendar shipDate) {
		this.shipDate = shipDate;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public BigDecimal getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}

	public BigDecimal getMsrpAtPurchase() {
		return msrpAtPurchase;
	}

	public void setMsrpAtPurchase(BigDecimal msrpAtPurchase) {
		this.msrpAtPurchase = msrpAtPurchase;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "OrderLine [id=" + id + ", orderId=" + orderId + ", quantity=" + quantity + ", shipDate="
				+ shipDate + ", status=" + status + ", totalValue=" + totalValue + ", msrpAtPurchase=" + msrpAtPurchase
				+ ", order=" + order + ", item=" + item + ", version=" + version + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, 
				item, 
				msrpAtPurchase, 
				// leave out order because of circular dependency! 
				// order, 
				orderId, 
				quantity, 
				shipDate, 
				status, 
				totalValue,
				version);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof OrderLine)) {
			return false;
		}
		OrderLine other = (OrderLine) obj;
		return Objects.equals(id, other.id) && Objects.equals(item, other.item)
				&& Objects.equals(msrpAtPurchase, other.msrpAtPurchase) && Objects.equals(order, other.order)
				&& Objects.equals(orderId, other.orderId) && quantity == other.quantity
				&& Objects.equals(shipDate, other.shipDate) && status == other.status
				&& Objects.equals(totalValue, other.totalValue) && Objects.equals(version, other.version);
	}

}
