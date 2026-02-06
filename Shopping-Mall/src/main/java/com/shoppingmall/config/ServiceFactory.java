package com.shoppingmall.config;

import com.shoppingmall.repository.ProductRepository;
import com.shoppingmall.repository.UserRepository;
import com.shoppingmall.repository.jdbc.JdbcProductRepository;
import com.shoppingmall.repository.jdbc.JdbcUserRepository;
import com.shoppingmall.service.AuthenticationService;
import com.shoppingmall.service.CartService;
import com.shoppingmall.service.OrderService;
import com.shoppingmall.service.ProductService;
import com.shoppingmall.service.impl.AuthenticationServiceImpl;
import com.shoppingmall.service.impl.CartServiceImpl;
import com.shoppingmall.service.impl.OrderServiceImpl;
import com.shoppingmall.service.impl.ProductServiceImpl;

/**
 * Very lightweight service locator used by JavaFX controllers.
 * <p>
 * It centralizes wiring of repositories and services to keep controllers thin
 * and respects DIP by exposing only service interfaces.
 */
public final class ServiceFactory {

    private static final UserRepository USER_REPOSITORY = new JdbcUserRepository();
    private static final ProductRepository PRODUCT_REPOSITORY = new JdbcProductRepository();

    private static final AuthenticationService AUTHENTICATION_SERVICE =
            new AuthenticationServiceImpl(USER_REPOSITORY);
    private static final ProductService PRODUCT_SERVICE =
            new ProductServiceImpl(PRODUCT_REPOSITORY);
    private static final CartService CART_SERVICE = new CartServiceImpl();
    private static final OrderService ORDER_SERVICE = new OrderServiceImpl();

    private ServiceFactory() {
    }

    public static AuthenticationService authenticationService() {
        return AUTHENTICATION_SERVICE;
    }

    public static ProductService productService() {
        return PRODUCT_SERVICE;
    }

    public static CartService cartService() {
        return CART_SERVICE;
    }

    public static OrderService orderService() {
        return ORDER_SERVICE;
    }
}



