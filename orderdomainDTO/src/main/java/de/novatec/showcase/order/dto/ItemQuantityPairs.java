package de.novatec.showcase.order.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="ItemQuantityPairs", description="POJO that represents a list with items and their corrensponding quantity.")
public class ItemQuantityPairs {

    @Schema(required = true)
    @NotNull
	private List<ItemQuantityPair> itemQuantityPairs;

	public List<ItemQuantityPair> getItemQuantityPairs() {
		return itemQuantityPairs;
	}

	public ItemQuantityPairs setItemQuantityPairs(List<ItemQuantityPair> itemQuantityPairs) {
		this.itemQuantityPairs = itemQuantityPairs;
		return this;
	}

}
