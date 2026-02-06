package com.shoppingmall.repository;

import com.shoppingmall.model.User;

import java.util.Optional;

/**
 * Abstraction over user persistence.
 * <p>
 * Depending on this interface instead of a concrete implementation
 * keeps higher layers open for extension and closed for modification.
 */
public interface UserRepository {

    Optional<User> findByUsername(String username);

    User save(User user);
}



