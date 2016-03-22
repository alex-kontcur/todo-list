package com.test.app.todolist.gui.panels.todo.comming;

import com.test.app.todolist.gui.panels.TabbedPanel;
import com.test.app.todolist.gui.panels.todo.TodoPanel;

/**
 * CommingTodoPanel
 *
 * @author Kontcur Alex (bona)
 * @since 14.07.11
 */
public class CommingTodoPanel extends TodoPanel {

    @Override
    protected TabbedPanel createTabbedPane() {
        return new CommingTabbedPanel();
    }
}
