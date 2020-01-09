package de.novatec.showcase.order.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="ItemQuantityPair", description="POJO that represents an item and its corrensponding quantity.")
public class ItemQuantityPair {

    @Schema(required = true)
	private Item item = null;
    @Schema(required = true)
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
