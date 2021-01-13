package com.rufino.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import com.rufino.server.model.Item;
import com.rufino.server.service.ItemService;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class ServerDeleteTests {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ItemService itemService;

    @BeforeEach
    void clearTable() {
        jdbcTemplate.update("DELETE FROM ITEMS");
    }

    @Test
    void itShouldDeleteItem() throws Exception {
        Item item1 = new Item();
        setItem(item1);
        saveAndAssert(item1);

        Item item2 = new Item();
        setItem(item2);
        saveAndAssert(item2, 1, 2);

        List<Item> itemsList = itemService.getAllItems();
        assertThat(itemsList.size()).isEqualTo(2);

        mockMvc.perform(delete("/api/v1/item/" + item1.getItemId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is("successfully operation")))
                .andExpect(status().isOk()).andReturn();

        itemsList = itemService.getAllItems();
        assertThat(itemsList.size()).isEqualTo(1);

    }
    ///////////////////////////////////////////////////////////////////////////////////
    private void setItem(Item item) {
        item.setItemName("Item 1");
        item.setItemPrice(10.99);
        item.setItemQuantity(5);
        item.setOrderId(UUID.fromString("d0ba6751-90a2-4c50-9661-aecd8360188e"));
        item.setProductId(UUID.fromString("0f5de10a-b22e-444f-93ca-bf1cb416d028"));
    }

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
