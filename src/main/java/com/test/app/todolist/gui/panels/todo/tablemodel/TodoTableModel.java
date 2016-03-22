package com.test.app.todolist.gui.panels.todo.tablemodel;

import com.google.common.eventbus.EventBus;
import com.test.app.todolist.domain.Todo;
import com.test.app.todolist.domain.repository.TodoRepository;
import com.test.app.todolist.event.TodoChangedEvent;
import com.test.app.todolist.gui.GuiContext;
import com.test.app.todolist.gui.GuiEventListener;
import com.test.app.todolist.gui.panels.TodoType;
import com.test.app.todolist.gui.util.GUITools;
import com.test.app.todolist.gui.util.TableUIUpdater;
import com.test.app.todolist.model.TodoManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TodoTableModel
 *
 * @author Kontcur Alex (bona)
 * @since 14.07.11
 */
@SuppressWarnings({"ReturnOfCollectionOrArrayField", "MethodOnlyUsedFromInnerClass"})
public abstract class TodoTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 827573496992235964L;

    protected TodoRepository todoRepository;
    protected TodoManager todoManager;

    protected List<Todo> data;
    protected String[] columns;
    protected EventBus eventBus;

    protected int lastCheckedRowIndex = -1;
    protected int lastCheckedColumnIndex = -1;

    private TableUIUpdater updater;
    private JTable table;

    protected TodoTableModel(TableUIUpdater updater, JTable table, TodoType todoType) {
        this.updater = updater;
        this.table = table;

        data = new ArrayList<>();

        GuiContext guiContext = GuiContext.get();
        todoRepository = guiContext.getTodoRepository();
        todoManager = GuiContext.get().getTodoManager();
        eventBus = guiContext.getEventBus();
        eventBus.register(this);

        GuiContext.get().addListener(TodoTableModel.class, new TodoChangedHandler(todoType));
    }

    protected abstract String[] getColumnNames();

    @Override
    public String getColumnName(int column) {
        return column < columns.length ? columns[column] : null;
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }
    
    @Override
    public int getRowCount() {
        return data.size();
    }

    public void clearBusy() {
        for (Todo todo : data) {
            todo.setBusy(false);
        }
    }

    public int getTodoIndex(Todo todo) {
        return data.indexOf(todo);
    }

    public void setData(Collection<Todo> todos) {
        data.clear();
        data.addAll(todos);
        fireTableDataChanged();
    }

    public List<Todo> getData() {
        return data;
    }

    public Todo getElementAt(int rowIndex) {
        return data.get(rowIndex);
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    protected void todoCreated(Todo todo) {
        int selected = getRowCount();
        if (data.contains(todo)) {
            return;
        }
        data.add(todo);
        fireTableStructureChanged();
        updater.updateTableUI();
        table.getSelectionModel().setSelectionInterval(0, selected);
    }

    protected int todoDeleted(Todo todo) {
        int userIndex = getTodoIndex(todo);
        data.remove(todo);
        lastCheckedRowIndex = -1;
        lastCheckedColumnIndex = -1;
        fireTableStructureChanged();
        int rowCount = getRowCount();
        int selected = GUITools.calculateNextSelected(rowCount, userIndex);
        updater.updateTableUI();
        updater.setTableSelection(selected);
        return rowCount;
    }

    protected void todoModified(Todo todo) {
        int index = data.indexOf(todo);
        data.remove(todo);
        data.add(index, todo);
        fireTableStructureChanged();

        index = getTodoIndex(todo);
        int rowCount = getRowCount();
        int selected = GUITools.calculateNextSelected(rowCount, index);
        updater.updateTableUI();
        updater.setTableSelection(selected);
    }

    private class TodoChangedHandler implements GuiEventListener<TodoChangedEvent> {

        private TodoType todoType;

        private TodoChangedHandler(TodoType todoType) {
            this.todoType = todoType;
        }

        @Override
        public void on(TodoChangedEvent event) {
            if (todoType.equals(event.getTodoType())) {
                Todo todo = event.getTodo();
                switch (event.getOperation()) {
                    case Create:
                        todoCreated(todo);
                        break;
                    case Delete:
                        todo.setBusy(false);
                        todoDeleted(todo);
                        break;
                    case Modify:
                        todoModified(todo);
                        break;
                    default:
                }
            }
        }
    }

}
