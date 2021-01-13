package com.rufino.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rufino.server.model.Item;
import com.rufino.server.service.ItemService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class ServerGetTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ItemService itemService;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void clearTable() {
        jdbcTemplate.update("DELETE FROM ITEMS");
    }

    @Test
    void itShouldGetAnItemById() throws Exception {

        MvcResult result = mockMvc.perform(get("/api/v1/item/")).andExpect(status().isOk()).andReturn();

        List<Item> itemList = Arrays
                .asList(objectMapper.readValue(result.getResponse().getContentAsString(), Item[].class));

        assertThat(itemList.size()).isEqualTo(0);

        Item item1 = new Item();
        setItem(item1);
        saveAndAssert(item1);

        Item item2 = new Item();
        setItem(item2);
        saveAndAssert(item2, 1, 2);

        result = mockMvc.perform(get("/api/v1/item/")).andExpect(status().isOk()).andReturn();

        itemList = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Item[].class));

        assertThat(itemList.size()).isEqualTo(2);

        mockMvc.perform(get("/api/v1/item/" + item1.getItemId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderId", Is.is("d0ba6751-90a2-4c50-9661-aecd8360188e")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId", Is.is("0f5de10a-b22e-444f-93ca-bf1cb416d028")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemName", Is.is("Item 1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemPrice", Is.is(10.99)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemQuantity", Is.is(5))).andExpect(status().isOk()).andReturn();

    }

    @Test
    void itShouldNotFoundItem_invalidUUID() throws Exception {
        Item item1 = new Item();
        setItem(item1);
        saveAndAssert(item1);

        Item item2 = new Item();
        setItem(item2);
        saveAndAssert(item2, 1, 2);

        mockMvc.perform(get("/api/v1/item/" + "abc"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is("Not OK")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.apiError", Is.is("Invalid item UUID format")))
                .andExpect(status().isBadRequest()).andReturn();

    }

    @Test
    void itShouldNotFoundItem_NotExists() throws Exception {
        Item item1 = new Item();
        setItem(item1);
        saveAndAssert(item1);

        Item item2 = new Item();
        setItem(item2);
        saveAndAssert(item2, 1, 2);

        mockMvc.perform(get("/api/v1/item/" + "8737659b-3b97-40c0-9529-f0741bba0eeb"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is("Not OK")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.apiError", Is.is("Item not found")))
                .andExpect(status().isNotFound()).andReturn();

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
