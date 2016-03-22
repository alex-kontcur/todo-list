package com.test.app.todolist.gui.panels.todo.tablemodel;

import com.test.app.todolist.domain.Todo;
import com.test.app.todolist.gui.panels.TodoType;
import com.test.app.todolist.gui.util.TableUIUpdater;

import javax.swing.*;

/**
 * ForeignTableModel - Table model which is contains watching todoitems information of current user
 *
 * @author Kontcur Alex (bona)
 * @since 11.07.11
 */
public class ForeignTableModel extends TodoTableModel {

    private static final long serialVersionUID = -3812666006686968060L;

    private static final int BOOLEAN_COLUMN_INDEX = 2;

    public ForeignTableModel(TableUIUpdater updater, JTable table) {
        super(updater, table, TodoType.Foreign);
        columns = getColumnNames();
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"Subject", "Assignee", "Is Done"};
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == BOOLEAN_COLUMN_INDEX ? Boolean.class : super.getColumnClass(columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < data.size()) {
            Todo todo = data.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return todo.getSubject();
                case 1:
                    return todo.getUser().getName();
                case 2:
                    return todo.isDone();
                default:
                    return null;
            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

}