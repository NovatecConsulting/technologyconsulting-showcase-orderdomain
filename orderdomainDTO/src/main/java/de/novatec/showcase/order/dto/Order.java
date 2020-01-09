package de.novatec.showcase.order.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonFormat;

import de.novatec.showcase.order.GlobalConstants;

@Schema(name="Order", description="POJO that represents a order.")
public class Order implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private OrderStatus status;

	@JsonFormat(pattern = GlobalConstants.DATE_FORMAT, locale = "de_DE")
	private Calendar shipDate;

	@JsonFormat(pattern = GlobalConstants.DATE_FORMAT, locale = "de_DE")
	private Calendar entryDate;

	private BigDecimal total;

	private BigDecimal discount;

//	private int orderLineCount;

	private List<OrderLine> orderLines;

	private Customer customer;

	private int version;

	protected Order() {
		super();
	}

	public Order(BigDecimal total, BigDecimal discount, Customer customer) {
		super();
		this.status = OrderStatus.DEFERRED;
		this.entryDate = Calendar.getInstance();
		this.total = total;
		this.discount = discount;
		this.version = 0;
		this.orderLines = new ArrayList<OrderLine>();
//		this.orderLineCount = 0;
		this.customer = customer;
	}

	public Integer getId() {
		return this.id;
	}

	public List<OrderLine> getOrderLines() {
		return this.orderLines;
	}

//	public OrderLine getOrderLine(int ol_id) {
//		return this.orderLines.get(ol_id - 1);
//	}

	public void addOrderLine(OrderLine ol) {
		this.orderLines.add(ol);
//		this.orderLineCount++;
	}

	public OrderStatus getStatus() {
		return this.status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public Calendar getShipDate() {
		return this.shipDate;
	}

	public void setShipDate(Calendar shipDate) {
		this.shipDate = shipDate;
	}

	public BigDecimal getTotal() {
		return this.total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public BigDecimal getDiscount() {
		return this.discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public Calendar getEntryDate() {
		return this.entryDate;
	}

	public int getVersion() {
		return version;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setEntryDate(Calendar entryDate) {
		this.entryDate = entryDate;
	}

	public void setOrderLines(List<OrderLine> orderLines) {
		this.orderLines = orderLines;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", status=" + status + ", shipDate=" + shipDate + ", entryDate=" + entryDate
				+ ", total=" + total + ", discount=" + discount + ", orderLineCount=" + orderLines.size() + ", orderLines="
				+ orderLines + ", customer=" + customer + ", version=" + version + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(customer, discount, entryDate, id, orderLines, shipDate, status, total,
				version);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Order)) {
			return false;
		}
		Order other = (Order) obj;
		return Objects.equals(customer, other.customer) && Objects.equals(discount, other.discount)
				&& Objects.equals(entryDate, other.entryDate) && Objects.equals(id, other.id)
				&& Objects.equals(orderLines, other.orderLines)
				&& Objects.equals(shipDate, other.shipDate) && status == other.status
				&& Objects.equals(total, other.total) && version == other.version;
	}
}
