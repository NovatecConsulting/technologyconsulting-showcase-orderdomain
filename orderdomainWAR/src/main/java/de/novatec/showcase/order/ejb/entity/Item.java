package de.novatec.showcase.order.ejb.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

@SuppressWarnings("serial")
@Entity
@Table(name = "O_Item")
@NamedQueries(value = { @NamedQuery(name = Item.BY_CATEGORY, query = Item.BY_CATEGORY_QUERY),
		@NamedQuery(name = Item.BY_ITEM_IDS, query = Item.BY_ITEM_IDS_QUERY),
		@NamedQuery(name = Item.COUNT_ITEMS, query = Item.COUNT_ITEMS_QUERY), })

public class Item implements Serializable {

	public static final String BY_CATEGORY = "QUERY_BY_CATEGORY";
	public static final String BY_ITEM_IDS = "QUERY_BY_ITEM_IDS";
	public static final String COUNT_ITEMS = "COUNT_ITEMS";

	public static final String BY_CATEGORY_QUERY = "Select i From Item i Where i.category=:category";
	public static final String BY_ITEM_IDS_QUERY = "Select i From Item i Where i.id in :ids";
	public static final String COUNT_ITEMS_QUERY = "SELECT COUNT(i) FROM Item i";

	@Id
	@Column(name = "I_ID")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "I_ID_GEN")
	@TableGenerator(name = "I_ID_GEN", table = "U_SEQUENCES", pkColumnName = "S_ID", valueColumnName = "S_NEXTNUM", pkColumnValue = "I_SEQ", allocationSize = 1)
	private String id;

	@Column(name = "I_NAME")
	private String name;

	@Column(name = "I_DESC")
	private String desc;

	@Column(name = "I_PRICE")
	private BigDecimal price;

	@Column(name = "I_DISCOUNT")
	private BigDecimal discount;

	@Column(name = "I_CATEGORY")
	private int category;

	@Version
	@Column(name = "I_VERSION")
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
