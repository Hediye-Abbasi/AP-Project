package com.shoppingmall.service;

import com.shoppingmall.model.User;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service responsible for user authentication and registration.
 * <p>
 * Exposed as an interface to allow different authentication strategies
 * (e.g. database, LDAP, in-memory) without changing callers.
 */
public interface AuthenticationService {

    Optional<User> login(String username, String rawPassword);

    User registerCustomer(String username,
                          String rawPassword,
                          String fullName,
                          String email,
                          double initialBalance);

    User updateBalance(User user, BigDecimal amount);
}


