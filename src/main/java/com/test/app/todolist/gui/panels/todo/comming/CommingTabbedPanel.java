package com.test.app.todolist.gui.panels.todo.comming;

import com.test.app.todolist.gui.panels.TabbedPanel;

/**
 * CommingTabbedPanel - Tabbed panel for inserting tab panel while general "comming" processing
 *
 * @author Kontcur Alex (bona)
 * @since 14.07.11
 */
public class CommingTabbedPanel extends TabbedPanel {

    public CommingTabbedPanel() {
        insertTab(0, new UserCommingTodoPanel());
        panels.get(0).activateTab();
    }

}
