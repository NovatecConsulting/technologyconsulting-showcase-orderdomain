package de.novatec.showcase.order.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;

import javax.json.bind.annotation.JsonbTransient;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="CustomerInventory", description="POJO that represents a customer inventory.")
public class CustomerInventory implements Comparator<CustomerInventory>, Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private Integer customerId;

	@JsonbTransient
	private Customer customer;

	private Item vehicle;

	private BigDecimal totalCost;

	private int quantity;

	private Integer version;

	public CustomerInventory() {
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

	public Integer getCustomerId() {
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

	public Integer getVersion() {
		return version;
	}

	@Override
	public int compare(CustomerInventory o1, CustomerInventory o2) {
		if (o1.equals(o2)) {
			return 0;
		} else if (o1.getCustomerId() < o2.getCustomerId()) {
			return -1;
		} else if (o1.getCustomerId() > o2.getCustomerId()) {
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
				// leave out customer cause of circular dependency!
				// customer,
				id, quantity, totalCost, vehicle, version);
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
		return Objects.equals(customerId, other.customerId) && Objects.equals(customer, other.customer)
				&& Objects.equals(id, other.id) && quantity == other.quantity
				&& Objects.equals(totalCost, other.totalCost) && Objects.equals(vehicle, other.vehicle)
				&& version == other.version;
	}

	@Override
	public String toString() {
		return "CustomerInventory [id=" + id + ", custId=" + customerId + ", customer=" + customer + ", vehicle="
				+ vehicle + ", totalCost=" + totalCost + ", quantity=" + quantity + ", version=" + version + "]";
	}
}
