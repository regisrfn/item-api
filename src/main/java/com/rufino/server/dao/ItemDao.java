package com.rufino.server.dao;

import java.util.List;
import java.util.UUID;

import com.rufino.server.model.Item;

public interface ItemDao {
    Item insertItem(Item item);

    int deleteItem(UUID id);

    List<Item> getAll();

    Item getItem(UUID id);

    Item updateItem(UUID id, Item item);

    List<Item> getItemFromOrder(UUID orderId);
}
