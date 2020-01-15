package de.novatec.showcase.order.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCart {

	private Map<String, ShoppingCartItem> shoppingCartItems = new HashMap<String, ShoppingCartItem>();

	private BigDecimal totalPrice = BigDecimal.ZERO;

	private BigDecimal totalDiscount = BigDecimal.ZERO;

	private BigDecimal totalOrginalPrice = BigDecimal.ZERO;

	public ShoppingCart() {
		super();
	}
	
	public BigDecimal getTotalOrginalPrice() {
		return totalOrginalPrice;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public BigDecimal getTotalDiscount() {
		return totalDiscount;
	}

	public boolean isEmpty() {
		return shoppingCartItems.isEmpty();
	}
	
	public List<ShoppingCartItem> getItems() {
		return new ArrayList<>(shoppingCartItems.values());
	}
	
	public Item getItem(String itemId) {
		return shoppingCartItems.get(itemId).getItem();
	}

	public int getItemCount() {
		return shoppingCartItems.size();
	}
	
	public BigDecimal getPrice(String itemId) {
		return calculatePrice(shoppingCartItems.get(itemId));
	}
	
	public void addItem(Item item, Integer quantity) {
		int currentQuantity = quantity;  
		if(shoppingCartItems.containsKey(item.getId()))
		{
			currentQuantity = shoppingCartItems.get(item.getId()).getQuantity() + quantity;
		}
		shoppingCartItems.put(item.getId(), new ShoppingCartItem(item, currentQuantity));

		totalOrginalPrice = calculateTotalOriginalPrice();
		totalPrice = calculateTotalPrice();
		totalDiscount = calculateTotalDiscount();
	}

	public void removeItem(String itemId) {
		
		if(shoppingCartItems.containsKey(itemId))
		{
			shoppingCartItems.remove(itemId);
			totalOrginalPrice = calculateTotalOriginalPrice();
			totalPrice = calculateTotalPrice();
			totalDiscount = calculateTotalDiscount();
		}
	}

	public void clear() {
		shoppingCartItems.clear();
		totalOrginalPrice = BigDecimal.ZERO;
		totalPrice = BigDecimal.ZERO;
		totalDiscount = BigDecimal.ZERO;
	}

	private BigDecimal calculatePrice(ShoppingCartItem shoppingCartItem) {
		Item item = shoppingCartItem.getItem();
		return item.getPrice().subtract(item.getDiscount())
				.multiply(BigDecimal.valueOf(shoppingCartItem.getQuantity()));
	}

	private BigDecimal calculateDiscount(ShoppingCartItem shoppingCartItem) {
		Item item = shoppingCartItem.getItem();
		return item.getDiscount().multiply(BigDecimal.valueOf(shoppingCartItem.getQuantity()));
	}

	private BigDecimal calculateTotalOriginalPrice()
	{
		BigDecimal totalOriginalPrice = BigDecimal.ZERO;
		for (ShoppingCartItem shoppingCartItem : shoppingCartItems.values()) {
			Item item = shoppingCartItem.getItem();
			totalOriginalPrice = totalOriginalPrice.add(item.getPrice().multiply(BigDecimal.valueOf(shoppingCartItem.getQuantity())));
		}
		return totalOriginalPrice;
	}

	private BigDecimal calculateTotalPrice()
	{
		BigDecimal totalPrice = BigDecimal.ZERO;
		for (ShoppingCartItem shoppingCartItem : shoppingCartItems.values()) {
			totalPrice = totalPrice.add(calculatePrice(shoppingCartItem));
		}
		return totalPrice;
	}

	private BigDecimal calculateTotalDiscount()
	{
		BigDecimal totalDiscount = BigDecimal.ZERO;
		for (ShoppingCartItem shoppingCartItem : getItems()) {
			totalDiscount = totalDiscount.add(calculateDiscount(shoppingCartItem));
		}
		return totalDiscount;
	}
	
	@Override
	public String toString() {
		return "ShoppingCart [shoppingCartItems=" + shoppingCartItems + ", totalPrice=" + totalPrice
				+ ", totalDiscount=" + totalDiscount + ", totalOrginalPrice=" + totalOrginalPrice + "]";
	}
}
