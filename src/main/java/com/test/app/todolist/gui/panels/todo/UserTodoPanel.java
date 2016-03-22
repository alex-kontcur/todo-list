package com.test.app.todolist.gui.panels.todo;

import com.test.app.todolist.domain.Todo;
import com.test.app.todolist.gui.Operation;
import com.test.app.todolist.event.TodoChangedEvent;
import com.test.app.todolist.gui.actions.CancelSearchAction;
import com.test.app.todolist.gui.actions.SearchAction;
import com.test.app.todolist.gui.panels.TodoListPanel;
import com.test.app.todolist.gui.panels.TodoType;
import com.test.app.todolist.gui.panels.todo.manage.AddTodoPanel;
import com.test.app.todolist.gui.panels.todo.manage.ManageTodoPanel;
import com.test.app.todolist.gui.panels.todo.manage.ModifyTodoPanel;
import com.test.app.todolist.gui.panels.todo.tablemodel.TodoTableModel;
import com.test.app.todolist.gui.panels.todo.tablemodel.UserTableModel;
import com.test.app.todolist.gui.util.BooleanColumnRenderer;
import com.test.app.todolist.gui.util.BoxLayoutTools;
import com.test.app.todolist.gui.util.GUITools;
import com.test.app.todolist.gui.util.TableUIUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * UserTodoPanel
 *
 * @author Kontcur Alex (bona)
 * @since 11.07.11
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class UserTodoPanel extends TodoListPanel implements TodoProvider {

    private static final Logger logger = LoggerFactory.getLogger(UserTodoPanel.class);

    protected UserTableModel tableModel;

    private TableUIUpdater uiUpdater = new UserTableUIUpdater();
    private SearchAction searchAction;
    private JTable todosTable;

    protected UserTodoPanel() {
        createGUI();
    }

    protected void createGUI() {
        setLayout(new BorderLayout());
        JPanel top = new JPanel(new BorderLayout(0, GUITools.STD_STRUT));
        top.setBorder(BorderFactory.createEmptyBorder(GUITools.STD_STRUT, GUITools.STD_STRUT, GUITools.STD_STRUT, GUITools.STD_STRUT));
        top.add(createTablePanel(), BorderLayout.CENTER);
        top.add(createButtonsPanel(), BorderLayout.PAGE_START);
        add(top, BorderLayout.CENTER);
    }

    protected abstract void initTableModel(TableUIUpdater uiUpdater, JTable table);

    private JScrollPane createTablePanel() {
        todosTable = new JTable();
        todosTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        initTableModel(uiUpdater, todosTable);

        todosTable.setModel(tableModel);
        todosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        todosTable.getSelectionModel().setSelectionInterval(0, 0);
        todosTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                todoSelected();
            }
        });
        todosTable.setDefaultRenderer(Boolean.class, new BooleanColumnRenderer(Color.yellow));
        GUITools.initTableColumnWidth(todosTable, getColumnsWidths(), 5, getFrameWidth());
        return new JScrollPane(todosTable);
    }

    protected abstract int[][] getColumnsWidths();

    @Override
    public void activateTab() {
        todoSelected();
        notifyClearTodo();
    }

    @Override
    public void panelClose() {
        super.panelClose();
        tableModel.clearBusy();
    }

    private void notifyClearTodo() {
        if (searchAction.isPreparingSearch()) {
            return;
        }
        int rowCount = todosTable.getModel().getRowCount();
        int selectedRow = todosTable.getSelectedRow();

        if (rowCount == 0 || selectedRow == -1) {
            eventBus.post(new TodoChangedEvent(null, TodoType.General, Operation.Clear));
            eventBus.post(new TodoChangedEvent(null, TodoType.Comming, Operation.Clear));
            eventBus.post(new TodoChangedEvent(null, TodoType.Foreign, Operation.Clear));
        }
    }

    private void todoSelected() {
        int rowCount = todosTable.getModel().getRowCount();
        int selectedRow = todosTable.getSelectedRow();

        boolean todoReady = searchAction.isPreparingSearch() || rowCount == 0 || selectedRow == -1;
        Todo todo = todoReady ? null : tableModel.getElementAt(selectedRow);
        if (todo != null) {
            selectedContext.setSelectedTodo(todo.getId());
            eventBus.post(new TodoChangedEvent(todo, TodoType.General, Operation.Select));
            eventBus.post(new TodoChangedEvent(todo, TodoType.Comming, Operation.Select));
        }
    }

    protected abstract void enableButtons(JButton... buttons);

    private JPanel createButtonsPanel() {
        JPanel p = BoxLayoutTools.createHorizontalPanel();

        JButton okButton = new JButton(createAddTodoAction("Create"));
        JButton cancelButton = new JButton(createDropTodoAction("Delete"));
        JButton modifyButton = new JButton(createModifyTodoAction("Modify"));

        enableButtons(okButton, modifyButton, cancelButton);

        JButton[] btn = {okButton, cancelButton};

        GUITools.makeSameSize(btn);
        p.add(okButton);
        p.add(Box.createHorizontalStrut(GUITools.STD_STRUT));
        p.add(modifyButton);
        p.add(Box.createHorizontalStrut(GUITools.STD_STRUT));
        p.add(cancelButton);

        p.add(Box.createHorizontalStrut(GUITools.BIG_STRUT));

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
        return tableModel.getData();
    }

    public void doClose() {
        Window window = SwingUtilities.windowForComponent(this);
        if (window != null) {
            window.dispose();
        }
    }

    private class UserTableUIUpdater implements TableUIUpdater {
        @Override
        public TodoTableModel getTableModel() {
            return tableModel;
        }
        @Override
        public void updateTableUI() {
            todoSelected();
            GUITools.initTableColumnWidth(todosTable, getColumnsWidths(), 5, getFrameWidth());
            todosTable.repaint();
        }
        @Override
        public void setTableSelection(int selected) {
            todosTable.getSelectionModel().setSelectionInterval(0, selected);
        }
        public JTable getTable() {
            return todosTable;
        }
    }

    @Override
    public String getCaption() {
        return "My todo tasks";
    }

    private Action createAddTodoAction(String name) {
        return new AbstractAction(name) {
            private static final long serialVersionUID = -4782138678683479611L;

            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info("Create todo action started..");

                ManageTodoPanel manageTodoPanel = new AddTodoPanel();
                windowProvider.openDialog("Add new todo", manageTodoPanel);
            }
        };
    }

    private Action createModifyTodoAction(String name) {
        return new AbstractAction(name) {
            private static final long serialVersionUID = 1775783991660586129L;

            @Override
            public void actionPerformed(ActionEvent e) {
                Todo todo = selectedContext.getSelectedTodo();
                if (todo == null) {
                    return;
                }
                logger.info("Modify todo action started..");
                ManageTodoPanel manageTodoPanel = new ModifyTodoPanel(todo);
                windowProvider.openDialog("Modify existing todo", manageTodoPanel);
            }
        };
    }

    private Action createDropTodoAction(String name) {
        return new AbstractAction(name) {
            private static final long serialVersionUID = 3234784925210376025L;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (tableModel.isEmpty()) {
                    return;
                }
                logger.info("Delete todo action started..");
                Todo todo = selectedContext.getSelectedTodo();
                todoRepository.delete(todo);
                selectedContext.setSelectedTodo(null);
                eventBus.post(new TodoChangedEvent(todo, TodoType.General, Operation.Delete));
            }
        };
    }

}
