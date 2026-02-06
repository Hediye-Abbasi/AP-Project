package com.shoppingmall.model;

import java.math.BigDecimal;

/**
 * Domain entity representing an application user.
 * <p>
 * This class is a simple data holder with no persistence or UI responsibilities,
 * adhering to SRP and forming part of the domain model in the layered architecture.
 */
public class User {

    private Integer id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private Role role;
    private BigDecimal balance;

    public User() {
    }

    public User(Integer id, String username, String passwordHash, String fullName,
                String email, Role role, BigDecimal balance) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.balance = balance;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}



