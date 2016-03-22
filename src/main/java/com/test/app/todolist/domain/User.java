package com.test.app.todolist.domain;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User
 *
 * @author Kontcur Alex (bona)
 * @since 25.11.13
 */
@SuppressWarnings({"ReturnOfCollectionOrArrayField", "AssignmentToCollectionOrArrayFieldFromParameter"})
public class User implements Serializable {

    private static final long serialVersionUID = -5859912036881139191L;

    private long id;
    private String name;
    private String password;
    private String email;
    private boolean admin;

    private Set<Todo> todos;
    private Set<Comment> comments;
    private Set<Todo> watchingTodos;

    protected User() {
    }

    public User(String name, String password, boolean admin) {
        this.name = name;
        this.admin = admin;
        this.password = DigestUtils.md5Hex(password);

        todos = new HashSet<>();
        comments = new HashSet<>();
        watchingTodos = new HashSet<>();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Collection<Todo> getTodos() {
        return todos;
    }

    public void setTodos(Set<Todo> todos) {
        this.todos = todos;
    }

    public void addTodo(Todo todo) {
        todos.add(todo);
        todo.addWatcher(this);
    }

    public Collection<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public Collection<Todo> getWatchingTodos() {
        return watchingTodos;
    }

    public void setWatchingTodos(Set<Todo> watchingTodos) {
        this.watchingTodos = watchingTodos;
    }

    public void addWatchingTodo(Todo todo) {
        watchingTodos.add(todo);
    }

    public void removeWatchingTodo(Todo todo) {
        watchingTodos.remove(todo);
    }

    @Override
    public String toString() {
        return "User [" + name + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        User user = (User) obj;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
