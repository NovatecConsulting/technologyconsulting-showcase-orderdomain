package de.novatec.showcase.ejb.orders.entity;

import java.io.Serializable;
import java.util.Objects;

public class CustomerInventoryPK implements Serializable {

	private static final long serialVersionUID = -8821786271744862267L;

	private Integer id;
	private Integer customerId;

	public CustomerInventoryPK(Integer id, Integer customerId) {
		super();
		this.id = id;
		this.customerId = customerId;
	}

	public CustomerInventoryPK() {
		super();
	}

	public Integer getId() {
		return id;
	}

	@Override
	public String toString() {
		return "CustomerInventoryPK [id=" + id + ", cuastomerId=" + customerId + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(customerId, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CustomerInventoryPK)) {
			return false;
		}
		CustomerInventoryPK other = (CustomerInventoryPK) obj;
		return Objects.equals(customerId, other.customerId) && Objects.equals(id, other.id);
	}

	public Integer getCustId() {
		return customerId;
	}

}
