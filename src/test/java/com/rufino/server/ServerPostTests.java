package com.rufino.server;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.hamcrest.core.Is;

@SpringBootTest
@AutoConfigureMockMvc
public class ServerPostTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void clearTable() {
        jdbcTemplate.update("DELETE FROM items");
    }

    @Test
    void itShouldSaveItem() throws Exception {
        JSONObject my_obj = new JSONObject();

        my_obj.put("orderId", "d0ba6751-90a2-4c50-9661-aecd8360188e");
        my_obj.put("productId", "0f5de10a-b22e-444f-93ca-bf1cb416d028");
        my_obj.put("itemName", "detergente liquido");
        my_obj.put("itemPrice", 1.99);
        my_obj.put("itemQuantity", 5);
        mockMvc.perform(post("/api/v1/item").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderId", Is.is("d0ba6751-90a2-4c50-9661-aecd8360188e")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId", Is.is("0f5de10a-b22e-444f-93ca-bf1cb416d028")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemName", Is.is("detergente liquido")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemPrice", Is.is(1.99)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemQuantity", Is.is(5))).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemPrice", Is.is(1.99))).andReturn();

    }

    @Test
    void itShouldSaveItem_withId() throws Exception {
        JSONObject my_obj = new JSONObject();

        my_obj.put("itemId", "d0ba6751-90a2-4c50-9661-aecd8360188e");
        my_obj.put("orderId", "d0ba6751-90a2-4c50-9661-aecd8360188e");
        my_obj.put("productId", "0f5de10a-b22e-444f-93ca-bf1cb416d028");
        my_obj.put("itemName", "detergente liquido");
        my_obj.put("itemPrice", 1.99);
        my_obj.put("itemQuantity", 5);
        mockMvc.perform(post("/api/v1/item").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.orderId", Is.is("d0ba6751-90a2-4c50-9661-aecd8360188e")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.productId", Is.is("0f5de10a-b22e-444f-93ca-bf1cb416d028")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemName", Is.is("detergente liquido")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemPrice", Is.is(1.99)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemQuantity", Is.is(5))).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.itemPrice", Is.is(1.99))).andReturn();

    }

    @Test
    void itShouldNotSaveItem() throws Exception {
        JSONObject my_obj = new JSONObject();
        my_obj.put("orderId", "d0ba6751-90a2-4c50-9661-aecd8360188e");
        my_obj.put("productId", "0f5de10a-b22e-444f-93ca-bf1cb416d028");
        my_obj.put("itemPrice", 1.99);
        my_obj.put("itemQuantity", 5);
        mockMvc.perform(post("/api/v1/item").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.itemName", Is.is("Value should not be empty")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is("Not OK")))
                .andExpect(status().isBadRequest()).andReturn();

        my_obj.put("itemName", "  ");
        mockMvc.perform(post("/api/v1/item").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.itemName", Is.is("Value should not be empty")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is("Not OK")))
                .andExpect(status().isBadRequest()).andReturn();

        my_obj.put("itemName", null);
        mockMvc.perform(post("/api/v1/item").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.itemName", Is.is("Value should not be empty")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is("Not OK")))
                .andExpect(status().isBadRequest()).andReturn();

        ///////////////////////////////////////////////////////////////////////////////
        my_obj = new JSONObject();
        mockMvc.perform(post("/api/v1/item").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.itemName", Is.is("Value should not be empty")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.itemPrice", Is.is("Value should not be empty")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.orderId", Is.is("Invalid order ID format")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.productId", Is.is("Invalid product ID format")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.itemQuantity", Is.is("Value should not be empty")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is("Not OK")))
                .andExpect(status().isBadRequest()).andReturn();

    }

    @Test
    void itShouldNotSaveItem_orderIdNotExists() throws Exception {
        JSONObject my_obj = new JSONObject();
        my_obj.put("orderId", "74868a48-cba9-40f6-921b-87cdf451a47f");
        my_obj.put("productId", "0f5de10a-b22e-444f-93ca-bf1cb416d028");
        my_obj.put("itemName", "detergente liquido");
        my_obj.put("itemPrice", 1.99);
        my_obj.put("itemQuantity", 5);
        mockMvc.perform(post("/api/v1/item").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is("Not OK")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.orderId", Is.is("Order id not exists")))
                .andExpect(status().isBadRequest()).andReturn();

    }

    @Test
    void itShouldNotSaveItem_productIdNotExists() throws Exception {
        JSONObject my_obj = new JSONObject();
        my_obj.put("orderId", "d0ba6751-90a2-4c50-9661-aecd8360188e");
        my_obj.put("productId", "483b7e0c-c1a3-4bfc-a3f6-7ee2b0941206");
        my_obj.put("itemName", "detergente liquido");
        my_obj.put("itemPrice", 1.99);
        my_obj.put("itemQuantity", 5);
        mockMvc.perform(post("/api/v1/item").contentType(MediaType.APPLICATION_JSON).content(my_obj.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Is.is("Not OK")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors.orderId", Is.is("Product id not exists")))
                .andExpect(status().isBadRequest()).andReturn();

    }

}
