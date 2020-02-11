package de.novatec.showcase.order.ejb.entity;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@Table(name = "O_ORDERLINE")
@Entity
@IdClass(OrderLinePK.class)
public class OrderLine {

	@Id
	@Column(name = "OL_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "OL_ID_GEN")
	@TableGenerator(name = "OL_ID_GEN", table = "U_SEQUENCES", pkColumnName = "S_ID", valueColumnName = "S_NEXTNUM", pkColumnValue = "OL_SEQ", allocationSize = 1)
	private Integer id;

	@Id
	@Column(name = "OL_O_ID", nullable = false)
	private Integer orderId;

	@Column(name = "OL_QTY")
	private int quantity;

	@Column(name = "OL_SHIP_DATE")
	@Temporal(value = TemporalType.DATE)
	private Calendar shipDate;

	@Column(name = "OL_STATUS")
	private OrderStatus status;

	@Column(name = "OL_TOTAL_VALUE", precision = 12, scale = 2)
	private BigDecimal totalValue;

	@Column(name = "OL_MSRP", precision = 12, scale = 2)
	private BigDecimal msrpAtPurchase;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OL_O_ID", insertable = false, updatable = false)
	private Order order;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OL_I_ID")
	private Item item;

	@Version
	@Column(name = "OL_VERSION")
	private Integer version;

	public OrderLine() {
		super();
	}

	public OrderLine(int quantity, BigDecimal totalValue, BigDecimal msrpAtPurchase, Order order, Item item) {
		super();
		this.orderId = order.getId();
		this.quantity = quantity;
		this.status = OrderStatus.DEFERRED;
		this.totalValue = totalValue;
		this.msrpAtPurchase = msrpAtPurchase;
		this.order = order;
		this.item = item;
	}

	public Order getOrder() {
		return order;
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

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
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

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return "OrderLine [id=" + id + ", orderId=" + orderId + ", quantity=" + quantity + ", shipDate=" + shipDate
				+ ", status=" + status + ", totalValue=" + totalValue + ", msrpAtPurchase=" + msrpAtPurchase + ", item="
				+ item + ", version=" + version + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(
				id, 
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
				&& Objects.equals(msrpAtPurchase, other.msrpAtPurchase) 
				&& Objects.equals(orderId, other.orderId) && quantity == other.quantity
				&& Objects.equals(shipDate, other.shipDate) && status == other.status
				&& Objects.equals(totalValue, other.totalValue) && Objects.equals(version, other.version);
	}

}
