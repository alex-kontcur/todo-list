package com.test.app.todolist.gui.actions;

import com.test.app.todolist.domain.Todo;
import com.test.app.todolist.gui.panels.todo.TodoProvider;
import com.test.app.todolist.gui.panels.todo.tablemodel.TodoTableModel;
import com.test.app.todolist.gui.util.TableUIUpdater;
import com.test.utils.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * SearchAction
 *
 * @author Kontcur Alex (bona)
 * @since 13.07.11
 */
public class SearchAction extends AbstractAction {

    private static final Logger logger = LoggerFactory.getLogger(SearchAction.class);

    private static final long serialVersionUID = -4782138678683479611L;

    private TableUIUpdater uiUpdater;

    private JTextField searchField;
    private TodoTableModel tableModel;
    private TodoProvider todoProvider;

    private volatile boolean preparingSearch;

    public SearchAction(TableUIUpdater uiUpdater, JTextField searchField, TodoProvider todoProvider) {
        super("Search");

        this.uiUpdater = uiUpdater;
        this.todoProvider = todoProvider;

        this.searchField = searchField;
        this.searchField.addActionListener(this);

        tableModel = uiUpdater.getTableModel();
    }

    public boolean isPreparingSearch() {
        return preparingSearch;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        logger.info("Search action started");

        String like = searchField.getText();
        preparingSearch = true;
        if (Helper.EMPTY_STRING.equals(like.trim())) {
            tableModel.setData(todoProvider.getCurrentTodos());
        } else {
            List<Todo> todos = new ArrayList<>();
            for (Todo todo : todoProvider.getCurrentTodos()) {
                if (todo.getSubject().contains(like)) {
                    todos.add(todo);
                }
            }
            tableModel.setData(todos);
        }
        uiUpdater.updateTableUI();
        preparingSearch = false;
        uiUpdater.setTableSelection(0);
    }

    public void clearText() {
        searchField.setText(Helper.EMPTY_STRING);
        searchField.repaint();
    }

}
