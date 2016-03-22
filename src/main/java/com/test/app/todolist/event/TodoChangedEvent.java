package com.test.app.todolist.event;

import com.test.app.todolist.domain.Todo;
import com.test.app.todolist.gui.Operation;
import com.test.app.todolist.gui.panels.TodoType;

/**
 * TodoChangedEvent
 *
 * @author Kontcur Alex (bona)
 * @since 28.11.13
 */
public class TodoChangedEvent {

    private Operation operation;
    private TodoType todoType;
    private Todo todo;

    public TodoChangedEvent(Todo todo, TodoType todoType, Operation operation) {
        this.todo = todo;
        this.todoType = todoType;
        this.operation = operation;
    }

    public Operation getOperation() {
        return operation;
    }

    public TodoType getTodoType() {
        return todoType;
    }

    public Todo getTodo() {
        return todo;
    }
}
