package de.novatec.showcase.order.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="Item", description="POJO that represents an item. An Item is an assembly in the manufacture domain. "
		+ "So the id of an item in the order domain and the id of an assembly in the manufacture domain are always equal to each other.")
public class Item implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String name;

	private String desc;

	private BigDecimal price;

	private BigDecimal discount;

	private int category;

	private Integer version;

	public Item() {
		super();
	}

	public Item(String name, String desc, BigDecimal price, BigDecimal discount, int category) {
		super();
		this.name = name;
		this.desc = desc;
		this.price = price;
		this.discount = discount;
		this.category = category;
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

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		return Objects.hash(category, desc, discount, id, name, price, version);
	}

	@Override
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

	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", desc=" + desc + ", price=" + price + ", discount=" + discount
				+ ", category=" + category + ", version=" + version + "]";
	}
}
