package com.rufino.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

	//////////////////// SAVE PRODUCT/////////////////////////////////
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

	///////////////////////////////////////////////////////////////////////////////////
	private void setItem(Item item) {
		item.setItemName("Item 1");
		item.setItemPrice(10.99);
		item.setItemQuantity(5);
		item.setOrderId(UUID.fromString("d0ba6751-90a2-4c50-9661-aecd8360188e"));
		item.setProductId(UUID.fromString("0f5de10a-b22e-444f-93ca-bf1cb416d028"));
	}
}
