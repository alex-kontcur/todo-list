package com.test.app.todolist.gui.panels.todo.tablemodel;

import com.test.app.todolist.domain.Todo;
import com.test.app.todolist.gui.panels.TodoType;
import com.test.app.todolist.gui.util.TableUIUpdater;
import org.joda.time.DateTime;

import javax.swing.*;
import java.util.Properties;

/**
 * UserTableModel
 *
 * @author Kontcur Alex (bona)
 * @since 14.07.11
 */
@SuppressWarnings ({"ReturnOfCollectionOrArrayField", "MagicNumber"})
public abstract class UserTableModel extends TodoTableModel {

    private static final long serialVersionUID = -262359745552609498L;

    private static final int DEFAULT_POSTPONE_DAYS = 1;
    private static final String POSTPONE_DAYS = "todo-list.postpone-days";

    protected Properties properties;
    protected int postponeDays;

    protected UserTableModel(TableUIUpdater updater, JTable table, Properties properties, TodoType todoType) {
        super(updater, table, todoType);
        this.properties = new Properties(properties);
        postponeDays = DEFAULT_POSTPONE_DAYS;
        try {
            postponeDays = Integer.parseInt(properties.getProperty(POSTPONE_DAYS));
        } catch (NumberFormatException ignored) {
        }
        columns = getColumnNames();
    }

    protected abstract boolean isColumnEditable(int columnIndex);

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return isColumnEditable(columnIndex) ? Boolean.class : super.getColumnClass(columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return isColumnEditable(columnIndex);
    }

    protected abstract void notifyValueChanged(Todo todo);

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (isColumnEditable(columnIndex)) {
            Todo todo = data.get(rowIndex);
            if (columnIndex == 1) {
                todo.setDone((Boolean) aValue);
            } else if (columnIndex == 2) {
                DateTime dateTime = new DateTime();
                todo.setStartDate(dateTime.plusDays(postponeDays));
                lastCheckedRowIndex = rowIndex;
                lastCheckedColumnIndex = 2;
            }
            notifyValueChanged(todoRepository.saveAndFlush(todo));
        } else {
            super.setValueAt(aValue, rowIndex, columnIndex);
        }
    }

}
