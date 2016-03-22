package com.test.app.todolist.model;

import com.test.app.todolist.domain.Todo;
import com.test.app.todolist.domain.User;
import com.test.app.todolist.domain.repository.TodoRepository;
import com.test.app.todolist.domain.repository.UserRepository;
import com.test.app.todolist.gui.GuiContext;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TodoManager
 *
 * @author Kontcur Alex (bona)
 * @since 09.07.11
 */
@Component
public class TodoManager {

    @Inject
    private TodoRepository todoRepository;

    @Inject
    private UserRepository userRepository;

    @Transactional
    public Todo createTodo(String subject, String description, DateTime startDate, Collection<User> watchers, boolean done) {
        Todo todo = new Todo(subject, description, startDate, GuiContext.get().getCurrentUser(), watchers, done);
        return todoRepository.save(todo);
    }

    public List<Todo> getPendingUserTodos() {
        return todoRepository.findByUserAndDone(GuiContext.get().getCurrentUser(), false);
    }

    public List<Todo> getDoneUserTodos() {
        return todoRepository.findByUserAndDone(GuiContext.get().getCurrentUser(), true);
    }

    public List<Todo> findByUser(User... users) {
        User user = users.length > 0 ? users[0] : GuiContext.get().getCurrentUser();
        return todoRepository.findByUser(user);
    }

    public List<Todo> findWatchingByUser(User... users) {
        User user = users.length > 0 ? users[0] : GuiContext.get().getCurrentUser();
        return new ArrayList<>(user.getWatchingTodos());
    }
}