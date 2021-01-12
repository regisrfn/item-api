package com.rufino.server.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import com.rufino.server.exception.ApiRequestException;
import com.rufino.server.model.Item;
import com.rufino.server.service.ItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/item")
@CrossOrigin
public class ItemController {

    private ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Item saveItem(@Valid @RequestBody Item item) {
        return itemService.saveItem(item);
    }

    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("{id}")
    public Item getItemById(@PathVariable String id) {
        try {
            UUID itemId = UUID.fromString(id);
            Item item = itemService.getItemById(itemId);
            if (item == null)
                throw new ApiRequestException("Item not found", HttpStatus.NOT_FOUND);
            return item;
        } catch (IllegalArgumentException e) {
            throw new ApiRequestException("Invalid item UUID format", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("{id}")
    public Map<String, String> deleteItemById(@PathVariable String id) {
        Map<String, String> message = new HashMap<>();

        try {
            UUID itemId = UUID.fromString(id);
            int op = itemService.deleteItemById(itemId);
            if (op == 0)
                throw new ApiRequestException("Item not found", HttpStatus.NOT_FOUND);
            message.put("message", "successfully operation");
            return message;
        } catch (IllegalArgumentException e) {
            throw new ApiRequestException("Invalid item UUID format", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("{id}")
    public Item updateItem(@PathVariable String id, @Valid @RequestBody Item item) {
        try {
            UUID itemId = UUID.fromString(id);
            item.setItemId(itemId);
            return itemService.updateItem(item);

        } catch (IllegalArgumentException e) {
            throw new ApiRequestException("Invalid item UUID format", HttpStatus.BAD_REQUEST);
        }
    }
}
