package com.test.app.todolist.gui.panels.todo.comming;

import com.test.app.todolist.gui.panels.todo.UserTodoPanel;
import com.test.app.todolist.gui.panels.todo.tablemodel.ComingTableModel;
import com.test.app.todolist.gui.util.TableUIUpdater;

import javax.swing.*;

/**
 * UserCommingTodoPanel
 *
 * @author Kontcur Alex (bona)
 * @since 14.07.11
 */
public class UserCommingTodoPanel extends UserTodoPanel {

    @Override
    protected void initTableModel(TableUIUpdater uiUpdater, JTable table) {
        tableModel = new ComingTableModel(uiUpdater, table, this, properties);
        tableModel.setData(dataContext.getComingTodos());
    }

    @Override
    protected int[][] getColumnsWidths() {
        return new int[][] {{0, 250}, {1, 30}, {2, 70}};
    }

    @Override
    protected void enableButtons(JButton... buttons) {
        for (JButton button : buttons) {
            button.setEnabled(false);
        }
    }

}
