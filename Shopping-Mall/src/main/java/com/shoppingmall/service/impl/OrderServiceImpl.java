package com.shoppingmall.service.impl;

import com.shoppingmall.config.DatabaseConfig;
import com.shoppingmall.model.CartItem;
import com.shoppingmall.model.Order;
import com.shoppingmall.model.OrderItem;
import com.shoppingmall.model.Product;
import com.shoppingmall.model.User;
import com.shoppingmall.service.OrderService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC-based implementation of {@link OrderService}.
 * <p>
 * Note: For simplicity transactions are handled manually within this class.
 */
public class OrderServiceImpl implements OrderService {

    @Override
    public Order checkout(User user, List<CartItem> items) {
        BigDecimal total = items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (user.getBalance().compareTo(total) < 0) {
            throw new RuntimeException("Insufficient balance to complete the purchase.");
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // update user balance
                BigDecimal newBalance = user.getBalance().subtract(total);
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE users SET balance = ? WHERE id = ?")) {
                    ps.setBigDecimal(1, newBalance);
                    ps.setInt(2, user.getId());
                    ps.executeUpdate();
                }

                // create order
                int orderId;
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO orders (user_id, total_amount) VALUES (?, ?) RETURNING id")) {
                    ps.setInt(1, user.getId());
                    ps.setBigDecimal(2, total);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        orderId = rs.getInt(1);
                    }
                }

                List<OrderItem> orderItems = new ArrayList<>();
                for (CartItem cartItem : items) {
                    Product product = cartItem.getProduct();
                    int quantity = cartItem.getQuantity();

                    // decrease stock
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE products SET stock_quantity = stock_quantity - ? WHERE id = ?")) {
                        ps.setInt(1, quantity);
                        ps.setInt(2, product.getId());
                        ps.executeUpdate();
                    }

                    // insert order item
                    BigDecimal unitPrice = product.getPrice();
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO order_items (order_id, product_id, quantity, unit_price) " +
                                    "VALUES (?, ?, ?, ?) RETURNING id")) {
                        ps.setInt(1, orderId);
                        ps.setInt(2, product.getId());
                        ps.setInt(3, quantity);
                        ps.setBigDecimal(4, unitPrice);
                        try (ResultSet rs = ps.executeQuery()) {
                            rs.next();
                            int orderItemId = rs.getInt(1);
                            orderItems.add(new OrderItem(orderItemId, product, quantity, unitPrice));
                        }
                    }
                }

                conn.commit();

                user.setBalance(newBalance);
                Order order = new Order();
                order.setId(orderId);
                order.setUser(user);
                order.setTotalAmount(total);
                order.setCreatedAt(LocalDateTime.now());
                order.setItems(orderItems);
                return order;
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Failed to complete checkout", e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to complete checkout", e);
        }
    }
}



