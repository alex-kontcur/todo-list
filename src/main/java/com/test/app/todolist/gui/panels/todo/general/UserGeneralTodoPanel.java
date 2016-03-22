package com.test.app.todolist.gui.panels.todo.general;

import com.test.app.todolist.gui.panels.todo.UserTodoPanel;
import com.test.app.todolist.gui.panels.todo.tablemodel.GeneralTableModel;
import com.test.app.todolist.gui.util.TableUIUpdater;

import javax.swing.*;

/**
 * UserGeneralTodoPanel
 *
 * @author Kontcur Alex (bona)
 * @since 14.07.11
 */
public class UserGeneralTodoPanel extends UserTodoPanel {

    @Override
    protected void initTableModel(TableUIUpdater uiUpdater, JTable table) {
        tableModel = new GeneralTableModel(uiUpdater, table, properties);
        tableModel.setData(todoManager.findByUser());
    }

    @Override
    protected int[][] getColumnsWidths() {
        return new int[][] {{0, 250}, {1, 30}};
    }

    @Override
    protected void enableButtons(JButton... buttons) {
        for (JButton button : buttons) {
            button.setEnabled(true);
        }
    }

}
