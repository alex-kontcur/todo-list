package com.test.app.todolist.event;

import com.test.app.todolist.domain.User;

/**
 * LoginEvent - Event for notifying login succesfull
 *
 * @author Kontcur Alex (bona)
 * @since 25.11.13
 */
public class LoginEvent {

    private User user;

    public LoginEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
