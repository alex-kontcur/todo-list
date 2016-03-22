package com.test.app.todolist.gui.panels.todo.tablemodel;

import com.test.app.todolist.domain.Todo;
import com.test.app.todolist.event.TodoChangedEvent;
import com.test.app.todolist.gui.Operation;
import com.test.app.todolist.gui.panels.TodoType;
import com.test.app.todolist.gui.util.TableUIUpdater;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * GeneralTableModel
 *
 * @author Kontcur Alex (bona)
 * @since 14.07.11
 */
public class GeneralTableModel extends UserTableModel {

   private static final long serialVersionUID = 2994641524340946656L;

    private List<Integer> defaultEditable;

    public GeneralTableModel(TableUIUpdater updater, JTable table, Properties properties) {
        super(updater, table, properties, TodoType.General);
        defaultEditable = Collections.singletonList(1);
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"Subject", "Is Done"};
    }

    @Override
    protected boolean isColumnEditable(int columnIndex) {
        return defaultEditable.contains(columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < data.size()) {
            Todo todo = data.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return todo.getSubject();
                case 1:
                    return todo.isDone();
                default:
                    return null;
            }
        }
        return null;
    }

    @Override
    protected void notifyValueChanged(Todo todo) {
        eventBus.post(new TodoChangedEvent(todo, TodoType.General, Operation.Modify));
    }

}
