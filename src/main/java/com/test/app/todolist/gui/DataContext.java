package com.test.app.todolist.gui;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.test.app.todolist.domain.Todo;
import com.test.app.todolist.domain.User;
import com.test.app.todolist.event.AdminLoginEvent;
import com.test.app.todolist.event.LoginEvent;
import com.test.app.todolist.event.TodoChangedEvent;
import com.test.app.todolist.model.TodoManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.*;

/**
 * DataContext
 *
 * @author Kontcur Alex (bona)
 * @since 28.11.13
 */
@SuppressWarnings("ReturnOfCollectionOrArrayField")
@Component
public class DataContext {

    @Inject
    private EventBus eventBus;

    @Inject
    private TodoManager todoManager;

    private List<Todo> userTodos;
    private Set<Todo> comingTodos;
    private List<Todo> foreignTodos;

    @PostConstruct
    public void init() {
        eventBus.register(this);
    }

    @Subscribe
    public void on(LoginEvent event) {
        User user = event.getUser();
        comingTodos = new HashSet<>();
        userTodos = new ArrayList<>(todoManager.findByUser(user));
        foreignTodos = new ArrayList<>(todoManager.findWatchingByUser(user));
    }

    @Subscribe
    public void on(AdminLoginEvent event) {
        User user = event.getUser();
        comingTodos = new HashSet<>();
        userTodos = new ArrayList<>(todoManager.findByUser(user));
        foreignTodos = new ArrayList<>(todoManager.findWatchingByUser(user));
    }

    @Subscribe
    public void on(TodoChangedEvent event) {
        Operation operation = event.getOperation();
        Todo todo = event.getTodo();
        switch (operation) {
            case Delete:
                comingTodos.remove(todo);
                break;
            default:
        }
    }

    public List<Todo> getUserTodos() {
        return userTodos;
    }

    public List<Todo> getForeignTodos() {
        return foreignTodos;
    }

    public Collection<Todo> getComingTodos() {
        return comingTodos;
    }

    public void addComingTodo(Todo todo) {
        comingTodos.add(todo);
    }
}
