package com.test.app.todolist.gui.panels.todo;

import com.test.app.todolist.domain.Todo;

import java.util.List;

/**
 * TodoProvider
 *
 * @author Kontcur Alex (bona)
 * @since 26.11.13
 */
public interface TodoProvider {

    List<Todo> getCurrentTodos();
}
