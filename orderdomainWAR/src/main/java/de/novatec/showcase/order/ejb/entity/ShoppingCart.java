package de.novatec.showcase.order.ejb.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.novatec.showcase.order.dto.Item;

public class ShoppingCart implements Serializable {

	private static final long serialVersionUID = 9045321423835602714L;

	private final Map<Item, Integer> itemQuantity = new HashMap<Item, Integer>();

	private BigDecimal totalPrice = BigDecimal.ZERO;
	private BigDecimal totalOrginalPrice = BigDecimal.ZERO;

	public ShoppingCart() {
		super();
	}

	public boolean isEmpty() {
		return this.itemQuantity.isEmpty();
	}

	public synchronized BigDecimal getTotalPrice() {
		BigDecimal totalPrice = BigDecimal.ZERO;
		for (Item item : this.getItems()) {
			totalPrice = totalPrice.add(this.calculatePrice(item.getPrice().subtract(item.getDiscount()),
					new BigDecimal(this.itemQuantity.get(item))));

		}
		return totalPrice;
	}

	public synchronized BigDecimal getTotalDiscount() {
		BigDecimal totalDiscount = BigDecimal.ZERO;
		for (Item item : this.getItems()) {
			totalDiscount = totalDiscount
					.add(this.calculatePrice(item.getDiscount(), new BigDecimal(this.itemQuantity.get(item))));
		}
		return totalDiscount;
	}

	public synchronized Item getItem(String itemId) {
		for (Item item : this.itemQuantity.keySet()) {
			if (item.getId().equals(itemId)) {
				return item;
			}
		}
		return null;
	}

	public synchronized List<Item> getItems() {
		return new ArrayList<>(this.itemQuantity.keySet());
	}

	public synchronized int getItemCount() {
		return this.itemQuantity.values().size();
	}

	public Integer getQuantity(Item item) {
		return this.itemQuantity.get(this.getItem(item.getId()));
	}

	public BigDecimal getPrice(Item item) {
		Item i = this.getItem(item.getId());
		BigDecimal discountPrice = item.getPrice().subtract(item.getDiscount());
		return new BigDecimal(this.itemQuantity.get(i)).multiply(discountPrice);
	}

	public synchronized void addItem(Item item, Integer quantity) {
		Item i = this.getItem(item.getId());
		if (i != null) {
			this.itemQuantity.put(i, quantity + this.itemQuantity.get(i));
			return;
		}

		this.itemQuantity.put(item, quantity);

		BigDecimal decQuantity = new BigDecimal(quantity);

		this.totalOrginalPrice = this.totalOrginalPrice.add(item.getPrice().multiply(decQuantity));
		BigDecimal discountPrice = item.getPrice().subtract(item.getDiscount());
		this.totalPrice = this.totalPrice.add(discountPrice.multiply(decQuantity));
	}

	public synchronized void removeItem(Item key) {
		Integer quantity = this.itemQuantity.get(this.getItem(key.getId()));

		if (quantity == null) {
			return;
		}

		this.itemQuantity.remove(key);

		BigDecimal decQuantity = new BigDecimal(quantity);

		this.totalOrginalPrice = this.totalOrginalPrice.subtract(key.getPrice().multiply(decQuantity));
		BigDecimal discountPrice = key.getPrice().subtract(key.getDiscount());
		this.totalPrice = this.totalPrice.subtract(discountPrice.multiply(decQuantity));

	}

	public synchronized void removeItem(String itemId) {
		Item tmp = this.getItem(itemId);
		this.removeItem(tmp);
	}

	public synchronized void clear() {
		this.itemQuantity.clear();
		this.totalOrginalPrice = BigDecimal.ZERO;
		this.totalPrice = BigDecimal.ZERO;
	}

	private BigDecimal calculatePrice(BigDecimal price, BigDecimal quantity) {
		return price.multiply(quantity);
	}
	
	@Override
	public String toString() {
		return "ShoppingCart [items=" + itemQuantity + ", totalPrice=" + totalPrice + ", totalOrginalPrice="
				+ totalOrginalPrice + "]";
	}

}
