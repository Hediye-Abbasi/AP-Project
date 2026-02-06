package com.shoppingmall.service.impl;

import com.shoppingmall.model.Role;
import com.shoppingmall.model.User;
import com.shoppingmall.repository.UserRepository;
import com.shoppingmall.service.AuthenticationService;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

/**
 * Default implementation of {@link AuthenticationService} using a {@link UserRepository}.
 */
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    public AuthenticationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> login(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .filter(user -> user.getPasswordHash().equals(hashPassword(rawPassword)));
    }

    @Override
    public User registerCustomer(String username, String rawPassword, String fullName,
                                 String email, double initialBalance) {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(hashPassword(rawPassword));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRole(Role.CUSTOMER);
        user.setBalance(BigDecimal.valueOf(initialBalance));
        return userRepository.save(user);
    }

    @Override
    public User updateBalance(User user, BigDecimal amount) {
        BigDecimal newBalance = user.getBalance().add(amount);
        user.setBalance(newBalance);
        return userRepository.save(user);
    }

    private String hashPassword(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}


