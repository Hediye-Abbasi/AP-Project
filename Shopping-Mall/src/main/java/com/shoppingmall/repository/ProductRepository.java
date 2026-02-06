package com.shoppingmall.repository;

import com.shoppingmall.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Repository abstraction for products.
 */
public interface ProductRepository {

    List<Product> findAll();

    Optional<Product> findById(int id);

    Product save(Product product);

    void deleteById(int id);

    List<Product> search(String nameOrCategory);
}



