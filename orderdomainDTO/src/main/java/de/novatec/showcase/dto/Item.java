package de.novatec.showcase.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class Item implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String name;

	private String desc;

	private BigDecimal price;

	private BigDecimal discount;

	private int category;

	private int version;

	public Item() {
		super();
	}

	public Item(String name, String desc, BigDecimal price, BigDecimal discount, int category, int version) {
		super();
		this.name = name;
		this.desc = desc;
		this.price = price;
		this.discount = discount;
		this.category = category;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return desc;
	}

	public void setDescription(String desc) {
		this.desc = desc;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public int getVersion() {
		return version;
	}

	public int hashCode() {
		return Objects.hash(category, desc, discount, id, name, price, version);
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Item)) {
			return false;
		}
		Item other = (Item) obj;
		return category == other.category && Objects.equals(desc, other.desc)
				&& Objects.equals(discount, other.discount) && Objects.equals(id, other.id)
				&& Objects.equals(name, other.name) && Objects.equals(price, other.price) && version == other.version;
	}

	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", desc=" + desc + ", price=" + price + ", discount=" + discount
				+ ", category=" + category + ", version=" + version + "]";
	}
}