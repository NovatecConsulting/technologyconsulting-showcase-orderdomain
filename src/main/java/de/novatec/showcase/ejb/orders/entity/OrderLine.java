package de.novatec.showcase.ejb.orders.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Objects;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Table(name = "O_ORDERLINE")
@Entity
public class OrderLine implements Serializable {

	private static final long serialVersionUID = -3556060565328926516L;

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "orderId", column = @Column(name = "OL_O_ID")),
			@AttributeOverride(name = "number", column = @Column(name = "OL_ID")) })
	private OrderLinePK pk;

	@Column(name = "OL_QTY")
	private int quantity;

	@Column(name = "OL_SHIP_DATE")
	@Temporal(value = TemporalType.DATE)
	private Calendar shipDate;

	@Column(name = "OL_STATUS")
	private OrderStatus status;

	@Column(name = "OL_TOTAL_VALUE")
	private BigDecimal totalValue;

	@Column(name = "OL_MSRP")
	private BigDecimal msrpAtPurchase;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OL_O_ID", insertable = false, updatable = false)
	@JsonIgnore
	private Order order;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OL_I_ID")
	private Item item;

	@Version
	@Column(name = "OL_VERSION")
	private int version;

	protected OrderLine() {
		super();
	}

	public OrderLine(int number, Integer orderId, int quantity, BigDecimal totalValue, BigDecimal msrpAtPurchase,
			Order order, Item item) {
		super();
		this.pk = new OrderLinePK(orderId, number);
		this.quantity = quantity;
		this.status = OrderStatus.DEFERRED;
		this.totalValue = totalValue;
		this.msrpAtPurchase = msrpAtPurchase;
		this.order = order;
		this.item = item;
		this.version = 0;
	}

	public OrderLinePK getPk() {
		return pk;
	}

	public Order getOrder() {
		return order;
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

	public int getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return "OrderLine [pk=" + pk + ", quantity=" + quantity + ", shipDate=" + shipDate + ", status=" + status
				+ ", totalValue=" + totalValue + ", msrpAtPurchase=" + msrpAtPurchase + ", order=" + order + ", item="
				+ item + ", version=" + version + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(
				item, 
				msrpAtPurchase, 
				// leave out order because of circular dependency! 
				// order, 
				pk, 
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
		return Objects.equals(item, other.item) && Objects.equals(msrpAtPurchase, other.msrpAtPurchase)
				&& Objects.equals(order, other.order) && Objects.equals(pk, other.pk) && quantity == other.quantity
				&& Objects.equals(shipDate, other.shipDate) && status == other.status
				&& Objects.equals(totalValue, other.totalValue) && version == other.version;
	}

}
