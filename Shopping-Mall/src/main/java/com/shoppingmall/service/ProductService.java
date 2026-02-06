package com.shoppingmall.service;

import com.shoppingmall.model.Product;

import java.util.List;

/**
 * Exposes use-cases around product catalog for both customers and administrators.
 */
public interface ProductService {

    List<Product> getAllProducts();

    List<Product> searchProducts(String query);

    List<Product> sortByName(List<Product> products, boolean ascending);

    List<Product> sortByPrice(List<Product> products, boolean ascending);

    Product createProduct(Product product);

    Product updateProduct(Product product);

    void deleteProduct(int productId);

    void updateStock(int productId, int newQuantity);
}



