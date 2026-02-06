package com.shoppingmall.service;

import com.shoppingmall.model.CartItem;
import com.shoppingmall.model.Order;
import com.shoppingmall.model.User;

import java.util.List;

/**
 * Handles checkout and order creation, including wallet balance validation.
 */
public interface OrderService {

    /**
     * Attempts to create an order for the given user and cart items.
     * Implementations must:
     * <ul>
     *     <li>Validate user balance &gt;= total price.</li>
     *     <li>Deduct the total amount from the user's balance.</li>
     *     <li>Persist the order and its items.</li>
     * </ul>
     *
     * @throws RuntimeException if the balance is insufficient or another error occurs.
     */
    Order checkout(User user, List<CartItem> items);
}



