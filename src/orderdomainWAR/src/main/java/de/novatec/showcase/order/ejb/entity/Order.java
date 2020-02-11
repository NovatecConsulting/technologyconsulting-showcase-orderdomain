package de.novatec.showcase.order.ejb.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@Entity
@Table(name = "O_ORDERS")
@NamedQueries(value = { 
		@NamedQuery(name = Order.COUNT_BY_CUSTOMER, query = Order.COUNT_BY_CUSTOMER_QUERY),
		@NamedQuery(name = Order.BY_CUSTOMER_AND_ORDER_STATUS, query = Order.BY_CUSTOMER_AND_ORDER_STATUS_QUERY),
		@NamedQuery(name = Order.BY_OUTSTANDING, query = Order.BY_OUTSTANDING_QUERY),
		@NamedQuery(name = Order.COUNT_BY_PERIOD, query = Order.COUNT_BY_PERIOD_QUERY) })
public class Order {
	
	public static final String COUNT_BY_CUSTOMER = "COUNT_BY_CUSTOMER";
	public static final String BY_CUSTOMER_AND_ORDER_STATUS = "QUERY_BY_CUSTOMER_AND_ORDER_STATUS";
	public static final String BY_OUTSTANDING = "QUERY_BY_OUTSTANDING";
	public static final String COUNT_BY_PERIOD = "COUNT_BY_PERIOD";

	public static final String COUNT_BY_CUSTOMER_QUERY = "SELECT COUNT(o) FROM Order o JOIN o.customer AS c WHERE c.id = :id";
	public static final String BY_CUSTOMER_AND_ORDER_STATUS_QUERY = "SELECT o FROM Order o JOIN o.customer AS c WHERE c.id = :id and o.status = :status";
	public static final String BY_OUTSTANDING_QUERY = "SELECT o FROM Order o WHERE o.status = de.novatec.showcase.order.ejb.entity.OrderStatus.DEFERRED OR "
			+ "o.status = de.novatec.showcase.order.ejb.entity.OrderStatus.PENDING_MANUFACTUR OR "
			+ "o.status = de.novatec.showcase.order.ejb.entity.OrderStatus.UNKNOWN";
	public static final String COUNT_BY_PERIOD_QUERY = "SELECT COUNT(o) FROM Order o WHERE o.entryDate > :startDate AND o.entryDate < :endDate";

	@Id
	@Column(name = "O_ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "O_ID_GEN")
	@TableGenerator(name = "O_ID_GEN", table = "U_SEQUENCES", pkColumnName = "S_ID", valueColumnName = "S_NEXTNUM", pkColumnValue = "O_SEQ", allocationSize = 1)
	private Integer id;

	@Column(name = "O_STATUS")
	@Enumerated(EnumType.ORDINAL)
	private OrderStatus status;

	@Column(name = "O_SHIP_DATE")
	@Temporal(value = TemporalType.DATE)
	private Calendar shipDate;

	@Column(name = "O_ENTRY_DATE")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Calendar entryDate;

	@Column(name = "O_TOTAL", precision = 12, scale = 2)
	private BigDecimal total;

	@Column(name = "O_DISCOUNT", precision = 12, scale = 2)
	private BigDecimal discount;

	@Column(name = "O_OL_CNT")
	private int orderLineCount;

	@OneToMany(mappedBy = "order", cascade = { CascadeType.REMOVE, CascadeType.PERSIST }, fetch = FetchType.EAGER)
	private List<OrderLine> orderLines;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "O_C_ID")
	private Customer customer;

	@Version
	@Column(name = "O_VERSION")
	private Integer version;

	public Order() {
		super();
	}

	public Order(BigDecimal total, BigDecimal discount, Customer customer) {
		super();
		this.status = OrderStatus.DEFERRED;
		this.entryDate = Calendar.getInstance();
		this.total = total;
		this.discount = discount;
		this.orderLines = new ArrayList<OrderLine>();
		this.orderLineCount = 0;
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
//
	public void addOrderLine(OrderLine ol) {
		this.orderLines.add(ol);
		this.orderLineCount++;
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

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getVersion() {
		return version;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public int getOrderLineCount() {
		return orderLineCount;
	}

	public void setOrderLineCount(int orderLineCount) {
		this.orderLineCount = orderLineCount;
	}

	public boolean isPriceMinusDiscountEqualPriceWithDiscount() {
		return getPrice().subtract(this.discount).equals(getPriceWithDiscount());
	}

	public boolean isPriceWithDiscountEqualTotal() {
		return getPriceWithDiscount().equals(this.total);
	}

	private BigDecimal getPrice() {
		BigDecimal price = BigDecimal.ZERO;
		for (OrderLine orderLine : this.getOrderLines()) {
			Item item = orderLine.getItem();
			price = price.add(item.getPrice().multiply(new BigDecimal(orderLine.getQuantity())));
		}
		return price;
	}

	private BigDecimal getPriceWithDiscount() {
		BigDecimal priceWithDiscount = BigDecimal.ZERO;
		for (OrderLine orderLine : this.getOrderLines()) {
			Item item = orderLine.getItem();
			BigDecimal discountPrice = item.getPrice().subtract(item.getDiscount());
			priceWithDiscount = priceWithDiscount.add(discountPrice.multiply(new BigDecimal(orderLine.getQuantity())));
		}
		return priceWithDiscount;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", status=" + status + ", shipDate=" + shipDate + ", entryDate=" + entryDate
				+ ", total=" + total + ", discount=" + discount + ", orderLineCount=" + orderLineCount + ", orderLines="
				+ orderLines + ", customer=" + customer + ", version=" + version + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(customer, discount, entryDate, id, orderLineCount, orderLines, shipDate, status, total,
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
				&& orderLineCount == other.orderLineCount && Objects.equals(orderLines, other.orderLines)
				&& Objects.equals(shipDate, other.shipDate) && status == other.status
				&& Objects.equals(total, other.total) && version == other.version;
	}
}
