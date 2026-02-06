package com.shoppingmall.controller;

import com.shoppingmall.model.User;

/**
 * Simple interface for controllers that need to be aware of the current user.
 */
public interface UserAwareController {

    void setCurrentUser(User user);
}



