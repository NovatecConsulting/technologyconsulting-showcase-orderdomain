package de.novatec.showcase.order.dto;

public class ShoppingCartItem {
	
	private Item item;
	
	private int quantity;

	public ShoppingCartItem(Item item, int quantity) {
		super();
		this.item = item;
		this.quantity = quantity;
	}

	public Item getItem() {
		return item;
	}

	public int getQuantity() {
		return quantity;
	}
	
	public void addQuantity(int quantity)
	{
		this.quantity = this.quantity + quantity;
	}

	@Override
	public String toString() {
		return "ShoppingCartItem [item=" + item + ", quantity=" + quantity + "]";
	}
}
