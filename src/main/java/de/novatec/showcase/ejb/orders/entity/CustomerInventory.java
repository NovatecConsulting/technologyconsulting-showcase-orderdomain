package de.novatec.showcase.ejb.orders.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
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
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "O_CUSTOMERINVENTORY")
@IdClass(CustomerInventoryPK.class)
public class CustomerInventory implements Comparator<CustomerInventory>, Serializable {

	private static final long serialVersionUID = 6026110084629827927L;

	@Id
	@Column(name = "CI_ID")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "INV_ID_GEN")
	@TableGenerator(name = "INV_ID_GEN", table = "U_SEQUENCES", pkColumnName = "S_ID", valueColumnName = "S_NEXTNUM", pkColumnValue = "INV_SEQ", allocationSize = 50)
	private Integer id;

	@Id
	@Column(name = "CI_CUSTOMERID")
	private Integer customerId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CI_CUSTOMERID", insertable = false, updatable = false)
	@JsonIgnore
	private Customer customer;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CI_ITEMID")
	private Item vehicle;

	@Column(name = "CI_VALUE")
	private BigDecimal totalCost;

	@Column(name = "CI_QUANTITY")
	private int quantity;

	@Version
	@Column(name = "CI_VERSION")
	private int version;

	protected CustomerInventory() {
		super();
	}

	public CustomerInventory(Customer customer, Item vehicle, BigDecimal totalCost, int quantity, int version) {
		super();
		this.customerId = customer.getId();
		this.customer = customer;
		this.vehicle = vehicle;
		this.totalCost = totalCost;
		this.quantity = quantity;
		this.version = version;
	}

	public Integer getId() {
		return id;
	}

	public Integer getCustId() {
		return customerId;
	}

	public Customer getCustomer() {
		return customer;
	}

	public Item getVehicle() {
		return vehicle;
	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public int getQuantity() {
		return quantity;
	}

	public void addQuantity(int quantity, BigDecimal price) {
		this.quantity += quantity;

		if (price.compareTo(BigDecimal.ZERO) > 0) {
			this.totalCost = this.totalCost.add(price.multiply(new BigDecimal(quantity)));
		} else {
			this.totalCost = this.totalCost.subtract(price.multiply(new BigDecimal(quantity)));
		}
	}

	public int getVersion() {
		return version;
	}

	@Override
	public int compare(CustomerInventory o1, CustomerInventory o2) {
		if (o1.equals(o2)) {
			return 0;
		} else if (o1.getCustId() < o2.getCustId()) {
			return -1;
		} else if (o1.getCustId() > o2.getCustId()) {
			return 1;
		} else {
			if (o1.getId() < o2.getId()) {
				return -1;
			} else {
				return 1;
			}
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(customerId, 
				//leave out customer cause of circular dependency!
				//customer,
				id, 
				quantity, 
				totalCost, 
				vehicle, 
				version);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CustomerInventory)) {
			return false;
		}
		CustomerInventory other = (CustomerInventory) obj;
		return Objects.equals(customerId, other.customerId) 
				&& Objects.equals(customer, other.customer)
				&& Objects.equals(id, other.id) 
				&& quantity == other.quantity
				&& Objects.equals(totalCost, other.totalCost) 
				&& Objects.equals(vehicle, other.vehicle)
				&& version == other.version;
	}

	@Override
	public String toString() {
		return "CustomerInventory [id=" + id + ", custId=" + customerId + ", customer=" + customer + ", vehicle=" + vehicle
				+ ", totalCost=" + totalCost + ", quantity=" + quantity + ", version=" + version + "]";
	}
}
