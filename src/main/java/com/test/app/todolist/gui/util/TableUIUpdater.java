package com.test.app.todolist.gui.util;

import com.test.app.todolist.gui.panels.todo.tablemodel.TodoTableModel;

/**
 * TableUIUpdater - Interceptor for gui operations with tables
 *
 * @author Kontcur Alex (bona)
 * @since 11.07.11
 */
public interface TableUIUpdater {

    void updateTableUI();

    TodoTableModel getTableModel();

    void setTableSelection(int selected);

}
