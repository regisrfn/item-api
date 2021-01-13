package com.rufino.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import com.rufino.server.exception.ApiHandlerException;
import com.rufino.server.model.Item;
import com.rufino.server.service.ItemService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class ServerApplicationTests {

	@Autowired
	private ItemService itemService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private ApiHandlerException apiHandlerException;

	@BeforeEach
	void clearTable() {
		jdbcTemplate.update("DELETE FROM ITEMS");

	}

	//////////////////// SAVE PRODUCT/////////////////////////////////
	@Test
	void itShouldSaveIntoDb() {
		Item item = new Item();
		setItem(item);
		long countBeforeInsert = jdbcTemplate.queryForObject("select count(*) from items", Long.class);
		assertEquals(0, countBeforeInsert);
		Item itemSaved = itemService.saveItem(item);
		long countAfterInsert = jdbcTemplate.queryForObject("select count(*) from items", Long.class);
		assertEquals(1, countAfterInsert);
		assertEquals(itemSaved.getItemName(), "Item 1");
		assertEquals(itemSaved.getItemPrice(), 10.99);
		assertEquals(itemSaved.getItemQuantity(), 5);
		assertEquals(itemSaved.getOrderId(), item.getOrderId());
		assertEquals(itemSaved.getProductId(), item.getProductId());
		assertEquals(itemSaved.getItemId(), item.getItemId());
	}

	@Test
	void itShouldNotSaveIntoDb_OrderIdNotExists() {
		Item item = new Item();
		setItem(item);
		item.setOrderId(UUID.fromString("74868a48-cba9-40f6-921b-87cdf451a47f"));
		long countBeforeInsert = jdbcTemplate.queryForObject("select count(*) from items", Long.class);
		assertEquals(0, countBeforeInsert);
		try {
			itemService.saveItem(item);
		} catch (DataIntegrityViolationException e) {
			assertEquals("{orderId=Order id not exists}", apiHandlerException.handleSqlError(e).toString());
		}
		long countAfterInsert = jdbcTemplate.queryForObject("select count(*) from items", Long.class);
		assertEquals(0, countAfterInsert);

	}

	//////////////////// GET ALL ITEMS/////////////////////////////////
	@Test
	void itShouldGetAllItems() {
		List<Item> itemsList = itemService.getAllItems();
		assertThat(itemsList.size()).isEqualTo(0);

		Item item1 = new Item();
		setItem(item1);
		saveAndAssert(item1);

		Item item2 = new Item();
		setItem(item2);
		saveAndAssert(item2, 1, 2);

		itemsList = itemService.getAllItems();
		assertThat(itemsList.size()).isEqualTo(2);
	}

	//////////////////// GET ITEM BY ID/////////////////////////////////
	@Test
	void itShouldGetAnItem() {
		Item item = new Item();
		setItem(item);
		saveAndAssert(item);

		assertThat(itemService.getItemById(item.getItemId())).isNotEqualTo(null);
		assertThat(itemService.getItemById(item.getItemId()).getOrderId())
				.isEqualTo(UUID.fromString("d0ba6751-90a2-4c50-9661-aecd8360188e"));
		assertThat(itemService.getItemById(item.getItemId()).getProductId())
				.isEqualTo(UUID.fromString("0f5de10a-b22e-444f-93ca-bf1cb416d028"));
		assertThat(itemService.getItemById(item.getItemId()).getItemName()).isEqualTo(item.getItemName());
		assertThat(itemService.getItemById(item.getItemId()).getItemPrice()).isEqualTo(item.getItemPrice());
		assertThat(itemService.getItemById(item.getItemId()).getItemQuantity()).isEqualTo(item.getItemQuantity());
	}

	@Test
	void itShouldNotGetAnItem() {
		Item item = new Item();
		setItem(item);
		saveAndAssert(item);

		assertThat(itemService.getItemById(UUID.fromString("846e1a32-f831-4bee-a6bc-673b5f901d7b"))).isEqualTo(null);

	}

	//////////////////// DELETE ORDER BY ID/////////////////////////////////
	@Test
	void itShouldDeleteItemById() {

		Item item1 = new Item();
		setItem(item1);
		saveAndAssert(item1);

		Item item2 = new Item();
		setItem(item2);
		saveAndAssert(item2, 1, 2);

		List<Item> itemsList = itemService.getAllItems();
		assertThat(itemsList.size()).isEqualTo(2);

		assertThat(itemService.deleteItemById(item1.getItemId())).isEqualTo(1);

		itemsList = itemService.getAllItems();
		assertThat(itemsList.size()).isEqualTo(1);

	}

	@Test
	void itShouldNotDeleteItemById_itemNotFound() {

		Item item1 = new Item();
		setItem(item1);
		saveAndAssert(item1);

		Item item2 = new Item();
		setItem(item2);
		saveAndAssert(item2, 1, 2);

		List<Item> itemsList = itemService.getAllItems();
		assertThat(itemsList.size()).isEqualTo(2);

		assertThat(itemService.deleteItemById(UUID.randomUUID())).isEqualTo(0);

		itemsList = itemService.getAllItems();
		assertThat(itemsList.size()).isEqualTo(2);

	}

	//////////////////// UPDATE ITEM BY ID/////////////////////////////////
	@Test
	void itShouldUpdateItem() {

		Item item1 = new Item();
		setItem(item1);
		saveAndAssert(item1);

		Item item2 = new Item();
		setItem(item2);
		saveAndAssert(item2, 1, 2);

		Item itemToUpdate = new Item();
		itemToUpdate.setItemId(item1.getItemId());
		itemToUpdate.setItemName("Detergente liquido");
		itemToUpdate.setItemPrice(1.99);
		itemToUpdate.setItemQuantity(2);
		itemToUpdate.setOrderId(UUID.fromString("0aa4a5ca-9312-498c-a423-7e62ab48215e"));
		itemToUpdate.setProductId(UUID.fromString("3b84e5c3-6313-4cfb-be06-2c120363a918"));
		itemService.updateItem(itemToUpdate);

		assertThat(itemService.getItemById(item1.getItemId())).isNotEqualTo(null);
		assertThat(itemService.getItemById(item1.getItemId()).getOrderId()).isEqualTo(itemToUpdate.getOrderId());
		assertThat(itemService.getItemById(item1.getItemId()).getProductId())
				.isEqualTo(itemToUpdate.getProductId());
		assertThat(itemService.getItemById(item1.getItemId()).getItemName()).isEqualTo(itemToUpdate.getItemName());
		assertThat(itemService.getItemById(item1.getItemId()).getItemPrice()).isEqualTo(itemToUpdate.getItemPrice());
		assertThat(itemService.getItemById(item1.getItemId()).getItemQuantity()).isEqualTo(itemToUpdate.getItemQuantity());
	}

	@Test
	void itShouldNotUpdateItem_orderNotExists() {
		Item item1 = new Item();
		setItem(item1);
		saveAndAssert(item1);

		Item item2 = new Item();
		setItem(item2);
		saveAndAssert(item2, 1, 2);

		item1.setOrderId(UUID.fromString("c6586b2e-a943-481f-a4e3-e768aff9e029"));
		try {
			itemService.updateItem(item1);
		} catch (DataIntegrityViolationException e) {
			// TODO: handle exception
		}
	}

	///////////////////////////////////////////////////////////////////////////////////
	private void setItem(Item item) {
		item.setItemName("Item 1");
		item.setItemPrice(10.99);
		item.setItemQuantity(5);
		item.setOrderId(UUID.fromString("d0ba6751-90a2-4c50-9661-aecd8360188e"));
		item.setProductId(UUID.fromString("0f5de10a-b22e-444f-93ca-bf1cb416d028"));
	}

	////////////////////////////////////////////////////////////////////////////////////
	private void saveAndAssert(Item item) {
		long countBeforeInsert = jdbcTemplate.queryForObject("select count(*) from items", Long.class);
		assertEquals(0, countBeforeInsert);
		itemService.saveItem(item);
		long countAfterInsert = jdbcTemplate.queryForObject("select count(*) from items", Long.class);
		assertEquals(1, countAfterInsert);
	}

	private void saveAndAssert(Item item, int before, int after) {
		long countBeforeInsert = jdbcTemplate.queryForObject("select count(*) from items", Long.class);
		assertEquals(before, countBeforeInsert);
		itemService.saveItem(item);
		long countAfterInsert = jdbcTemplate.queryForObject("select count(*) from items", Long.class);
		assertEquals(after, countAfterInsert);
	}
}
