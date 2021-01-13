package com.rufino.server;

import com.rufino.server.model.Item;
import com.rufino.server.service.ItemService;

import org.hamcrest.core.Is;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class ServerPutTests {
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
    void itShouldUpdateItem() throws Exception {
        JSONObject my_obj = new JSONObject();

        Item item1 = new Item();
        setItem(item1);
        saveAndAssert(item1);

        Item item2 = new Item();
        setItem(item2);
        saveAndAssert(item2, 1, 2);

        my_obj.put("orderId", "0aa4a5ca-9312-498c-a423-7e62ab48215e");
        my_obj.put("productId", "3b84e5c3-6313-4cfb-be06-2c120363a918");
        my_obj.put("itemName", "detergente liquido");
        my_obj.put("itemPrice", 1.99);
        my_obj.put("itemQuantity", 10);

        mockMvc.perform(put("/api/v1/item/" + item1.getItemId()).contentType(MediaType.APPLICATION_JSON)
                .content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderId", Is.is("0aa4a5ca-9312-498c-a423-7e62ab48215e")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId", Is.is("3b84e5c3-6313-4cfb-be06-2c120363a918")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemName", Is.is("detergente liquido")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemPrice", Is.is(1.99)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemQuantity", Is.is(10))).andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void itShouldUpdateSaveItem_orderIdNotExists() throws Exception {
        JSONObject my_obj = new JSONObject();

        Item item1 = new Item();
        setItem(item1);
        saveAndAssert(item1);

        Item item2 = new Item();
        setItem(item2);
        saveAndAssert(item2, 1, 2);

        my_obj.put("orderId", "74868a48-cba9-40f6-921b-87cdf451a47f");
        my_obj.put("productId", "0f5de10a-b22e-444f-93ca-bf1cb416d028");
        my_obj.put("itemName", "detergente liquido");
        my_obj.put("itemPrice", 1.99);
        my_obj.put("itemQuantity", 5);

        mockMvc.perform(put("/api/v1/item/" + item1.getItemId()).contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is("Not OK")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.orderId", Is.is("Order id not exists")))
                .andExpect(status().isBadRequest()).andReturn();

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
