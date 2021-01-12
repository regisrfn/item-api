package com.rufino.server.repository;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rufino.server.dao.ItemDao;
import com.rufino.server.dao.JpaDao;
import com.rufino.server.exception.ApiRequestException;
import com.rufino.server.model.Item;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ItemRepository implements ItemDao {

    private JpaDao jpaDataAccess;
    private JdbcTemplate jdbcTemplate;
    private ObjectMapper om;

    @Autowired
    public ItemRepository(JpaDao jpaDataAccess, JdbcTemplate jdbcTemplate) {
        this.jpaDataAccess = jpaDataAccess;
        this.jdbcTemplate = jdbcTemplate;
        this.om = new ObjectMapper().registerModule(new JavaTimeModule());
        ;

    }

    @Override
    public Item insertItem(Item item) {
        return jpaDataAccess.save(item);
    }

    @Override
    public int deleteItem(UUID id) {
        try {
            jpaDataAccess.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public List<Item> getAll() {
        return jpaDataAccess.findAll();
    }

    @Override
    public Item getItem(UUID id) {
        return jpaDataAccess.findById(id).orElse(null);
    }

    @Override
    public Item updateItem(UUID id, Item item) {
        String itemString;

        try {
            itemString = om.writeValueAsString(item);
            String sql = generateSqlUpdate(item, itemString);
            int result = jdbcTemplate.update(sql + "where item_id = ?", id);
            return (result > 0 ? getItem(id) : null);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ApiRequestException(e.getMessage());
        }
    }

    private String generateSqlUpdate(Item item, String itemString) throws JSONException {
        String sql = "UPDATE PRODUCTS SET ";
        JSONObject jsonObject = new JSONObject(itemString);
        Iterator<String> keys = jsonObject.keys();
        if (!keys.hasNext()) {
            throw new ApiRequestException("No valid data to update");
        }
        while (keys.hasNext()) {
            String key = keys.next();
            if (key.equals("itemName") || key.equals("itemDescription"))
                sql = sql + key.replaceAll("([A-Z])", "_$1").toLowerCase() + "='" + jsonObject.get(key) + "' ";
            else if (key.equals("itemCreatedAt"))
                sql = sql + key.replaceAll("([A-Z])", "_$1").toLowerCase() + "='" + item.getItemCreatedAt().toString()
                        + "' ";
            else
                sql = sql + key.replaceAll("([A-Z])", "_$1").toLowerCase() + "=" + jsonObject.get(key) + " ";

            if (keys.hasNext()) {
                sql = sql + ", ";
            }
        }
        return sql;
    }

}
