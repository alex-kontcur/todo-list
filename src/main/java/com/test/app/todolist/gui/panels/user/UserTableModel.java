package com.test.app.todolist.gui.panels.user;

import com.test.app.todolist.domain.User;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * UserTableModel
 *
 * @author Kontcur Alex (bona)
 * @since 11.07.11
 */
public class UserTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 6733016092925778971L;

    private final List<User> data;
    private final String[] cols;

    public UserTableModel() {
        data = new ArrayList<>();
        cols = getColumnNames();
    }    

    private static String[] getColumnNames() {
        return new String[] {"Name", "E-mail", "Admin"};
    }

    public void addUser(User user) {
        if (data.contains(user)) {
            return;
        }
        data.add(user);
        fireTableStructureChanged();
    }

    public void deleteUser(User user) {
        data.remove(user);
        fireTableStructureChanged();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 2 ? Boolean.class : super.getColumnClass(columnIndex);
    }

    public int getUserIndex(User user) {
        return data.indexOf(user);
    }

    public void setData(List<User> users) {
        data.clear();
        data.addAll(users);
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        return column < cols.length ? cols[column] : null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0 || super.isCellEditable(rowIndex, columnIndex);
    }

    public User getElementAt(int rowIndex) {
        return data.get(rowIndex);
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return cols.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < data.size()) {
            User user = data.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return user.getName();
                case 1:
                    return user.getEmail();
                case 2:
                    return user.isAdmin();
                default:
                    return null;
            }
        }
        return null;
    }

}

