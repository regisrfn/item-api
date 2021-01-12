package com.rufino.server.service;

import java.util.List;
import java.util.UUID;

import com.rufino.server.dao.ItemDao;
import com.rufino.server.exception.ApiRequestException;
import com.rufino.server.model.Item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    private ItemDao itemDao;

    @Autowired
    public ItemService(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    public Item saveItem(Item item) {
        return itemDao.insertItem(item);
    }

    public List<Item> getAllItems() {
        return itemDao.getAll();
    }

    public Item getItemById(UUID id) {
        return itemDao.getItem(id);
    }

    public int deleteItemById(UUID id) {
        return itemDao.deleteItem(id);
    }

    public Item updateItem(UUID id, Item item) {
        try {
            item.setItemId(null);
            return itemDao.updateItem(id, item);
        } catch (DataIntegrityViolationException e) {
            throw new ApiRequestException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    public Item updateItem(Item item) {
        return itemDao.insertItem(item);
    }
}
