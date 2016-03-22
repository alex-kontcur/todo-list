package com.test.app.todolist.event;

import com.test.app.todolist.domain.User;
import com.test.app.todolist.gui.Operation;

/**
 * UserChangedEvent - Event for notifying then User entity changes
 *
 * @author Kontcur Alex (bona)
 * @since 28.11.13
 */
public class UserChangedEvent {

    private User user;
    private Operation operation;

    public UserChangedEvent(User user, Operation operation) {
        this.user = user;
        this.operation = operation;
    }

    public User getUser() {
        return user;
    }

    public Operation getOperation() {
        return operation;
    }
}
