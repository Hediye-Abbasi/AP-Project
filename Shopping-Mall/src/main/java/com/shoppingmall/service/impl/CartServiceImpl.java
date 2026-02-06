package com.shoppingmall.service.impl;

import com.shoppingmall.model.CartItem;
import com.shoppingmall.model.Product;
import com.shoppingmall.service.CartService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple in-memory implementation of {@link CartService} for the current session.
 */
public class CartServiceImpl implements CartService {

    private final List<CartItem> items = new ArrayList<>();

    @Override
    public void addToCart(Product product) {
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        items.add(new CartItem(product, 1));
    }

    @Override
    public void removeFromCart(Product product) {
        items.removeIf(i -> i.getProduct().getId().equals(product.getId()));
    }

    @Override
    public void increaseQuantity(Product product) {
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
    }

    @Override
    public void decreaseQuantity(Product product) {
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(product.getId())) {
                int newQuantity = item.getQuantity() - 1;
                if (newQuantity <= 0) {
                    items.remove(item);
                } else {
                    item.setQuantity(newQuantity);
                }
                return;
            }
        }
    }

    @Override
    public void clearCart() {
        items.clear();
    }

    @Override
    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public BigDecimal getTotalAmount() {
        return items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}


