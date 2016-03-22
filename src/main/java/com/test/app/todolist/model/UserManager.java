package com.test.app.todolist.model;

import com.test.app.todolist.domain.Todo;
import com.test.app.todolist.domain.User;
import com.test.app.todolist.domain.repository.TodoRepository;
import com.test.app.todolist.domain.repository.UserRepository;
import com.test.app.todolist.gui.GuiContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * UserManager
 *
 * @author Kontcur Alex (bona)
 * @since 09.07.11
 */
@Component
public class UserManager {

    private static final Logger logger = LoggerFactory.getLogger(UserManager.class);

    @Inject
    private TodoRepository todoRepository;

    @Inject
    private UserRepository userRepository;

    public List<User> findAllExceptLogged() {
        List<User> users = userRepository.findAll();
        User currentUser = GuiContext.get().getCurrentUser();
        if (currentUser != null) {
            users.remove(currentUser);
        }
        return users;
    }

    @Transactional
    public User createUser(String name, String password, String email, boolean admin) {
        User user = userRepository.findUserByName(name);
        if (user != null) {
            return user;
        }
        user = new User(name, password, admin);
        user.setEmail(email);
        return userRepository.save(user);
    }

    @Transactional
    public boolean deleteUser(User user) {
        boolean deleted = true;
        for (Todo todo :  new ArrayList<>(user.getWatchingTodos())) {
            todo.removeWatcher(user);
            todoRepository.save(todo);
        }
        user.setWatchingTodos(null);
        try {
        userRepository.delete(user);
        } catch (Exception e) {
            logger.error("Error while deleting user [" + user + "] ->", e);
        }
        return deleted;
    }

}
