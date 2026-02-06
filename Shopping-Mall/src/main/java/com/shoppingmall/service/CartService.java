package com.shoppingmall.service;

import com.shoppingmall.model.CartItem;
import com.shoppingmall.model.Product;

import java.math.BigDecimal;
import java.util.List;

/**
 * Manages the in-memory shopping cart for the active user session.
 */
public interface CartService {

    void addToCart(Product product);

    void removeFromCart(Product product);

    void increaseQuantity(Product product);

    void decreaseQuantity(Product product);

    void clearCart();

    List<CartItem> getItems();

    BigDecimal getTotalAmount();
}


