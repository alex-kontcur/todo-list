package com.test.app.todolist.gui.panels.todo.tablemodel;

import com.test.app.todolist.domain.Todo;
import com.test.app.todolist.gui.Operation;
import com.test.app.todolist.event.TodoChangedEvent;
import com.test.app.todolist.gui.panels.TodoType;
import com.test.app.todolist.gui.panels.todo.comming.UserCommingTodoPanel;
import com.test.app.todolist.gui.util.TableUIUpdater;
import com.test.utils.Helper;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * ComingTableModel
 *
 * @author Kontcur Alex (bona)
 * @since 11.07.11
 */
public class ComingTableModel extends UserTableModel {

    private static final long serialVersionUID = 278034261931618883L;
    
    private List<Integer> freshEditable;
    private UserCommingTodoPanel panel;

    public ComingTableModel(TableUIUpdater updater, JTable table, UserCommingTodoPanel panel, Properties properties) {
        super(updater, table, properties, TodoType.Comming);
        this.panel = panel;

        freshEditable = Arrays.asList(1, 2);
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"Subject", "Is Done", "Postpone for " + postponeDays + " days"};
    }

    @Override
    protected boolean isColumnEditable(int columnIndex) {
        return freshEditable.contains(columnIndex);
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
                case 2:
                    return lastCheckedRowIndex == rowIndex && lastCheckedColumnIndex == columnIndex;
                default:
                    return null;
            }
        }
        return null;
    }

    @Override
    protected void notifyValueChanged(Todo todo) {
        eventBus.post(new TodoChangedEvent(todo, TodoType.Comming, Operation.Delete));
    }

    protected int dataChangedDelete(Todo todo) {
        Helper.sleep(TimeUnit.SECONDS, 2);
        int rowCount = todoDeleted(todo);
        todo.setBusy(false);

        if (rowCount == 0) {
            panel.waitCursor();
            panel.doClose();
        }
        return rowCount;
    }

}
