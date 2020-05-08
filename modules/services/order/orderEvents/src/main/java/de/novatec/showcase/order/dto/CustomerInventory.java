package de.novatec.showcase.order.dto;

import java.math.BigDecimal;
import java.util.Objects;

import javax.json.bind.annotation.JsonbTransient;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Schema(name="CustomerInventory", description="POJO that represents a customer inventory.")
public class CustomerInventory{

	private Integer id;

	private Integer customerId;

	@JsonbTransient
	@JsonIgnore
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

	public void setId(Integer id) {
		this.id = id;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public void setVehicle(Item vehicle) {
		this.vehicle = vehicle;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
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

	public void setVersion(Integer version) {
		this.version = version;
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
