package de.novatec.showcase.order.dto;

import java.util.List;

public class ItemQuantityPairs {

	private List<ItemQuantityPair> itemQuantityPairs;

	public List<ItemQuantityPair> getItemQuantityPairs() {
		return itemQuantityPairs;
	}

	public ItemQuantityPairs setItemQuantityPairs(List<ItemQuantityPair> itemQuantityPairs) {
		this.itemQuantityPairs = itemQuantityPairs;
		return this;
	}

}
