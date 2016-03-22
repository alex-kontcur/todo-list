package com.test.app.todolist.gui.panels.todo.general;

import com.test.app.todolist.gui.panels.TabbedPanel;
import com.test.app.todolist.gui.panels.todo.TodoPanel;

/**
 * GeneralTodoPanel - Parent panel for panel which are shows details of the selected todoitem when there is general todoprocessing
 *
 * @author Kontcur Alex (bona)
 * @since 14.07.11
 */
public class GeneralTodoPanel extends TodoPanel {

    @Override
    protected TabbedPanel createTabbedPane() {
        return new GeneralTabbedPanel();
    }
}
