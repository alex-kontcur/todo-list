package com.test.app.todolist.gui.panels.todo.general;

import com.test.app.todolist.gui.panels.TabbedPanel;
import com.test.app.todolist.gui.panels.todo.foreign.ForeignTodoPanel;

/**
 * GeneralTabbedPanel - Tabbed panel for inserting tab panel when there is general todoprocessing
 *
 * @author Kontcur Alex (bona)
 * @since 14.07.11
 */
public class GeneralTabbedPanel extends TabbedPanel {

    public GeneralTabbedPanel() {
        insertTab(0, new UserGeneralTodoPanel());
        insertTab(1, new ForeignTodoPanel());
        panels.get(0).activateTab();
    }

}
