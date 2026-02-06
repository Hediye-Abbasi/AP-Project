package com.shoppingmall.service.impl;

import com.shoppingmall.model.Product;
import com.shoppingmall.repository.ProductRepository;
import com.shoppingmall.service.ProductService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link ProductService}.
 */
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> searchProducts(String query) {
        if (query == null || query.isBlank()) {
            return getAllProducts();
        }
        return productRepository.search(query.trim());
    }

    @Override
    public List<Product> sortByName(List<Product> products, boolean ascending) {
        Comparator<Product> comparator = Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
        if (!ascending) {
            comparator = comparator.reversed();
        }
        return products.stream().sorted(comparator).collect(Collectors.toList());
    }

    @Override
    public List<Product> sortByPrice(List<Product> products, boolean ascending) {
        Comparator<Product> comparator = Comparator.comparing(Product::getPrice);
        if (!ascending) {
            comparator = comparator.reversed();
        }
        return products.stream().sorted(comparator).collect(Collectors.toList());
    }

    @Override
    public Product createProduct(Product product) {
        product.setId(null);
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(int productId) {
        productRepository.deleteById(productId);
    }

    @Override
    public void updateStock(int productId, int newQuantity) {
        productRepository.findById(productId).ifPresent(p -> {
            p.setStockQuantity(newQuantity);
            productRepository.save(p);
        });
    }
}



