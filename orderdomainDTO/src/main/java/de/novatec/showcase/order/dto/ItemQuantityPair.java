package de.novatec.showcase.order.dto;

/**
 * Simple class for holding an <code>Item</code> and it's quantity
 */
public class ItemQuantityPair {

	private Item item = null;
	private int quantity = 0;

	public ItemQuantityPair() {
		super();
	}

	public ItemQuantityPair(Item item, int quantity) {
		super();
		this.item = item;
		this.quantity = quantity;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
