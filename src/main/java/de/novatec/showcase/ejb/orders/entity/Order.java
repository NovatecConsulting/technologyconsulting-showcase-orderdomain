package de.novatec.showcase.ejb.orders.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import javax.json.bind.annotation.JsonbTransient;
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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "O_ORDERS")
@NamedQueries(value = { @NamedQuery(name = "COUNT_BY_CUSTOMER", query = Order.COUNT_BY_CUSTOMER),
		@NamedQuery(name = "QUERY_BY_CUSTOMER_AND_ORDER_STATUS", query = Order.QUERY_BY_CUSTOMER_AND_ORDER_STATUS),
		@NamedQuery(name = "QUERY_BY_OUTSTANDING", query = Order.QUERY_BY_OUTSTANDING),
		@NamedQuery(name = "COUNT_BY_PERIOD", query = Order.COUNT_BY_PERIOD) })
public class Order implements Serializable {

	private static final long serialVersionUID = 4097897801304129622L;
	public static final String DATE_FORMAT = "yyyy-MM-dd";

	public static final String COUNT_BY_CUSTOMER = "SELECT COUNT(o) FROM Order o JOIN o.customer AS c WHERE c.id = :id";
	public static final String QUERY_BY_CUSTOMER_AND_ORDER_STATUS = "SELECT o FROM Order o JOIN o.customer AS c WHERE c.id = :id and o.status = :status";
	public static final String QUERY_BY_OUTSTANDING = "SELECT o FROM Order o WHERE o.status = de.novatec.showcase.ejb.orders.entity.OrderStatus.DEFERRED OR "
			+ "o.status =de.novatec.showcase.ejb.orders.entity.OrderStatus.PENDING_MANUFACTUR OR "
			+ "o.status =de.novatec.showcase.ejb.orders.entity.OrderStatus.UNKNOWN";
	public static final String COUNT_BY_PERIOD = "SELECT COUNT(o) FROM Order o WHERE o.entryDate > :startDate AND o.entryDate < :endDate";

	@Id
	@Column(name = "O_ID")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "O_ID_GEN")
	@TableGenerator(name = "O_ID_GEN", table = "U_SEQUENCES", pkColumnName = "S_ID", valueColumnName = "S_NEXTNUM", pkColumnValue = "O_SEQ", allocationSize = 1)
	private Integer id;

	@Column(name = "O_STATUS")
	@Enumerated(EnumType.ORDINAL)
	private OrderStatus status;

	@Column(name = "O_SHIP_DATE")
	@Temporal(value = TemporalType.DATE)
	@JsonFormat(pattern = DATE_FORMAT, locale = "de_DE")
	private Calendar shipDate;

	@Column(name = "O_ENTRY_DATE")
	@Temporal(value = TemporalType.TIMESTAMP)
	@JsonFormat(pattern = DATE_FORMAT, locale = "de_DE")
	private Calendar entryDate;

	@Column(name = "O_TOTAL")
	private BigDecimal total;

	@Column(name = "O_DISCOUNT")
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
		this.orderLineCount = 0;
		this.customer = customer;
	}

	public Integer getId() {
		return this.id;
	}

	public List<OrderLine> getOrderLines() {
		return this.orderLines;
	}

	public OrderLine getOrderLine(int ol_id) {
		return this.orderLines.get(ol_id - 1);
	}

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

	public int getVersion() {
		return version;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	@JsonIgnore
	@JsonbTransient
	public boolean isPriceMinusDiscountEqualPriceWithDiscount() {
		return getPrice().subtract(this.discount).equals(getPriceWithDiscount());
	}

	@JsonIgnore
	@JsonbTransient
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
