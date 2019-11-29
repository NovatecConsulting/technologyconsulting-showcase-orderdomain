package de.novatec.showcase.ejb.orders.session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import de.novatec.showcase.ejb.orders.entity.Item;

/**
 * Session Bean implementation class ItemBrowserSession
 */

@Stateful
public class ItemSession implements ItemSessionLocal {

	@PersistenceContext
	EntityManager em;

	private int batchSize = 10;
	private int currentMax = 0;
	private int currentMin = 1;
	private static final String PART_NAME_PREFIX = "PARTS";

	private List<Item> getSublist() {
		List<Item> items = new ArrayList<Item>();
		for (int i = 0; i < this.getBatchSize(); i++) {
			Item item = em.find(Item.class, PART_NAME_PREFIX + (this.currentMin + i));
			items.add(item);
		}

		return items;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void setBatchSize(int size) {
		this.batchSize = size;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public int getBatchSize() {
		return this.batchSize;
	}

	@SuppressWarnings("unchecked")
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Item> getItems(String itemIds) {

		Query itemsQuery = em.createNamedQuery("QUERY_BY_ITEM_IDS");
		itemsQuery.setParameter("ids", idsAsLong(Arrays.asList(StringUtils.stripEnd(itemIds, ",").split(",", 10))));
		return itemsQuery.getResultList();
	}

	private List<Long> idsAsLong(List<String> ids) {
		List<Long> longIds = new ArrayList<Long>();
		for (String id : ids) {
			longIds.add(Long.valueOf(id));
		}
		return longIds;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Item> browseForward() {
		this.currentMin = this.currentMax + 1;
		this.currentMax = this.currentMax + this.batchSize;

		return this.getSublist();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Item> browseReverse() {
		this.currentMin = this.currentMin - this.batchSize;
		this.currentMax = this.currentMax - this.batchSize;

		if (this.currentMin < 1) {
			this.currentMin = this.currentMin + this.batchSize;
			this.currentMax = this.currentMax + this.batchSize;
			return new ArrayList<Item>();
		}

		return this.getSublist();

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public long getTotalItems() {
		Query totalQuery = em.createQuery("SELECT COUNT(i) FROM Item i");
		return (Long) totalQuery.getSingleResult();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String getCurrentMax() {
		return String.valueOf(this.currentMax);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String getCurrentMin() {
		return String.valueOf(this.currentMin);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String createItem(Item item) {
		em.persist(item);
		em.flush();
		return item.getId();
	}

}
