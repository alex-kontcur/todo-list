package com.test.app.todolist.gui.panels.todo.foreign;

import com.test.app.todolist.domain.Todo;
import com.test.app.todolist.gui.Operation;
import com.test.app.todolist.event.TodoChangedEvent;
import com.test.app.todolist.gui.GuiContext;
import com.test.app.todolist.gui.actions.CancelSearchAction;
import com.test.app.todolist.gui.actions.SearchAction;
import com.test.app.todolist.gui.panels.TodoListPanel;
import com.test.app.todolist.gui.panels.TodoType;
import com.test.app.todolist.gui.panels.todo.TodoProvider;
import com.test.app.todolist.gui.panels.todo.tablemodel.ForeignTableModel;
import com.test.app.todolist.gui.panels.todo.tablemodel.TodoTableModel;
import com.test.app.todolist.gui.util.BooleanColumnRenderer;
import com.test.app.todolist.gui.util.BoxLayoutTools;
import com.test.app.todolist.gui.util.GUITools;
import com.test.app.todolist.gui.util.TableUIUpdater;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ForeignTodoPanel
 *
 * @author Kontcur Alex (bona)
 * @since 14.07.11
 */
public class ForeignTodoPanel extends TodoListPanel implements TodoProvider {

    private ForeignTableModel tableModel;
    private SearchAction searchAction;
    private TableUIUpdater uiUpdater;
    private JTable otherTodosTable;

    public ForeignTodoPanel() {
        uiUpdater = new ForeignTableUIUpdater();
        createGUI();
    }

    private void createGUI() {
        setLayout(new BorderLayout());
        JPanel top = new JPanel(new BorderLayout(0, GUITools.STD_STRUT));
        top.setBorder(BorderFactory.createEmptyBorder(GUITools.STD_STRUT, GUITools.STD_STRUT, GUITools.STD_STRUT, GUITools.STD_STRUT));
        top.add(createTablePanel(), BorderLayout.CENTER);
        top.add(createButtonsPanel(), BorderLayout.PAGE_START);

        uiUpdater.updateTableUI();

        add(top, BorderLayout.CENTER);
    }

    protected void initTableModel() {
        tableModel = new ForeignTableModel(uiUpdater, otherTodosTable);
        tableModel.setData(GuiContext.get().getCurrentUser().getWatchingTodos());
    }

    private JScrollPane createTablePanel() {
        otherTodosTable = new JTable();
        otherTodosTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        initTableModel();

        otherTodosTable.setModel(tableModel);
        otherTodosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        otherTodosTable.getSelectionModel().setSelectionInterval(0, 0);
        otherTodosTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                todoSelected();
            }
        });
        otherTodosTable.setDefaultRenderer(Boolean.class, new BooleanColumnRenderer(null));

        return new JScrollPane(otherTodosTable);
    }

    private JPanel createButtonsPanel() {
        JPanel p = BoxLayoutTools.createHorizontalPanel();

        p.add(Box.createHorizontalStrut(256));
        p.add(new JLabel("Subject like : "));
        p.add(Box.createHorizontalStrut(GUITools.STD_STRUT));
        JTextField searchField = new JTextField(15);
        p.add(searchField);
        p.add(Box.createHorizontalStrut(GUITools.STD_STRUT));
        searchAction = new SearchAction(uiUpdater, searchField, this);
        CancelSearchAction cancelSearch = new CancelSearchAction(searchAction);
        p.add(new JButton(searchAction));
        p.add(Box.createHorizontalStrut(GUITools.SMALL_STRUT));
        p.add(new JButton(cancelSearch));
        return p;
    }

    @Override
    public List<Todo> getCurrentTodos() {
        return new ArrayList<>(GuiContext.get().getCurrentUser().getWatchingTodos());
    }

    @Override
    public void activateTab() {
        todoSelected();
        notifyClearTodo();
    }

    private void notifyClearTodo() {
        if (searchAction.isPreparingSearch()) {
            return;
        }
        int rowCount = otherTodosTable.getModel().getRowCount();
        int selectedRow = otherTodosTable.getSelectedRow();
        if (rowCount == 0 || selectedRow == -1) {
            eventBus.post(new TodoChangedEvent(null, TodoType.Foreign, Operation.Clear));
        }
    }    

    private void todoSelected() {
        int rowCount = otherTodosTable.getModel().getRowCount();
        int selectedRow = otherTodosTable.getSelectedRow();
        Todo selected = searchAction.isPreparingSearch() || rowCount == 0 || selectedRow == -1 ? null : tableModel.getElementAt(selectedRow);
        if (selected != null) {
            selectedContext.setSelectedTodo(selected.getId());
            eventBus.post(new TodoChangedEvent(selected, TodoType.Foreign, Operation.Select));
        }
    }

    @Override
    public String getCaption() {
        return "Other todo tasks";
    }

    private class ForeignTableUIUpdater implements TableUIUpdater {
        @Override
        public TodoTableModel getTableModel() {
            return tableModel;
        }
        @Override
        public void updateTableUI() {
            todoSelected();
            GUITools.initTableColumnWidth(otherTodosTable, new int[][] {{0, 250}, {1, 50}, {2, 30}}, 5, getFrameWidth());
            otherTodosTable.repaint();
        }
        @Override
        public void setTableSelection(int selected) {
            otherTodosTable.getSelectionModel().setSelectionInterval(0, selected);
        }
    }


}
