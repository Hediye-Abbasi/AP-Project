package com.shoppingmall.repository.jdbc;

import com.shoppingmall.config.DatabaseConfig;
import com.shoppingmall.model.Product;
import com.shoppingmall.repository.ProductRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC-based product repository.
 */
public class JdbcProductRepository implements ProductRepository {

    @Override
    public List<Product> findAll() {
        String sql = "SELECT * FROM products WHERE active = TRUE ORDER BY name";
        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(mapRow(rs));
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch products", e);
        }
    }

    @Override
    public Optional<Product> findById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch product by id", e);
        }
        return Optional.empty();
    }

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            return insert(product);
        } else {
            return update(product);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete product", e);
        }
    }

    @Override
    public List<Product> search(String nameOrCategory) {
        String sql = "SELECT * FROM products WHERE active = TRUE AND " +
                "(LOWER(name) LIKE ? OR LOWER(category) LIKE ?) ORDER BY name";
        List<Product> products = new ArrayList<>();
        String pattern = "%" + nameOrCategory.toLowerCase() + "%";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search products", e);
        }
    }

    private Product insert(Product product) {
        String sql = "INSERT INTO products (name, description, category, price, stock_quantity, image_url, active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setString(3, product.getCategory());
            ps.setBigDecimal(4, product.getPrice());
            ps.setInt(5, product.getStockQuantity());
            ps.setString(6, product.getImageUrl());
            ps.setBoolean(7, product.isActive());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    product.setId(rs.getInt(1));
                }
            }
            return product;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert product", e);
        }
    }

    private Product update(Product product) {
        String sql = "UPDATE products SET name = ?, description = ?, category = ?, price = ?, " +
                "stock_quantity = ?, image_url = ?, active = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setString(3, product.getCategory());
            ps.setBigDecimal(4, product.getPrice());
            ps.setInt(5, product.getStockQuantity());
            ps.setString(6, product.getImageUrl());
            ps.setBoolean(7, product.isActive());
            ps.setInt(8, product.getId());
            ps.executeUpdate();
            return product;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update product", e);
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setCategory(rs.getString("category"));
        BigDecimal price = rs.getBigDecimal("price");
        product.setPrice(price != null ? price : BigDecimal.ZERO);
        product.setStockQuantity(rs.getInt("stock_quantity"));
        product.setImageUrl(rs.getString("image_url"));
        product.setActive(rs.getBoolean("active"));
        return product;
    }
}



