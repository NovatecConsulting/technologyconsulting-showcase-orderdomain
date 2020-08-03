package de.novatec.showcase.order.ejb.session;

import java.util.List;

import de.novatec.showcase.order.ejb.entity.Item;
import de.novatec.showcase.order.ejb.session.exception.ItemNotFoundException;

public interface ItemService {
	public void setBatchSize(int Size);

	public int getBatchSize();

	public List<Item> getItems(String itemIds);

	public List<Item> browseForward();

	public List<Item> browseReverse();

	public long getTotalItems();

	public String getCurrentMax();

	public String getCurrentMin();

	Item createItem(Item item);
	
	public Item cancelItem(Integer itemId) throws ItemNotFoundException;


}
