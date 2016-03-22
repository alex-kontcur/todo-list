package com.test.app.todolist.event;

import com.test.app.todolist.domain.User;

/**
 * AdminLoginEvent
 *
 * @author Kontcur Alex (bona)
 * @since 26.11.13
 */
public class AdminLoginEvent {

    private User user;

    public AdminLoginEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
