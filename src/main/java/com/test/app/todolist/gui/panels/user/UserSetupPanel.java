package com.test.app.todolist.gui.panels.user;

import com.test.app.todolist.domain.User;
import com.test.app.todolist.gui.Operation;
import com.test.app.todolist.event.UserChangedEvent;
import com.test.app.todolist.gui.GuiContext;
import com.test.app.todolist.gui.GuiEventListener;
import com.test.app.todolist.gui.panels.TodoListPanel;
import com.test.app.todolist.gui.util.BooleanColumnRenderer;
import com.test.app.todolist.gui.util.BoxLayoutTools;
import com.test.app.todolist.gui.util.GUITools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * UserSetupPanel
 *
 * @author Kontcur Alex (bona)
 * @since 11.07.11
 */
public class UserSetupPanel extends TodoListPanel {

    private static final Logger logger = LoggerFactory.getLogger(UserSetupPanel.class);

    private UserTableModel tableModel;
    private JTable usersTable;

    public UserSetupPanel() {
        setFrameWidth(400);
        setFrameHeight(400);

        createGUI();

        GuiContext.get().addListener(UserSetupPanel.class, new UserChangedHandler());
    }

    private void createGUI() {
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout(0, GUITools.STD_STRUT));
        top.setBorder(BorderFactory.createEmptyBorder(GUITools.STD_STRUT,
            GUITools.STD_STRUT, GUITools.STD_STRUT, GUITools.STD_STRUT));

        top.add(createButtonsPanel(), BorderLayout.PAGE_START);
        top.add(createTablePanel(), BorderLayout.CENTER);

        add(top, BorderLayout.CENTER);
    }

    private JScrollPane createTablePanel() {
        usersTable = new JTable();
        usersTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableModel = new UserTableModel();
        tableModel.setData(userRepository.findAll());
        usersTable.setModel(tableModel);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersTable.getSelectionModel().setSelectionInterval(0, 0);
        usersTable.setDefaultRenderer(Boolean.class, new BooleanColumnRenderer(null));

        GUITools.initTableColumnWidth(usersTable, new int[][] {{0, 40}, {1, 70}, {2, 30}}, 5, getFrameWidth());

        return new JScrollPane(usersTable);
    }

    private JPanel createButtonsPanel() {
        JPanel p = BoxLayoutTools.createHorizontalPanel();

        JButton okButton = new JButton(createAddUserAction("Add user"));
        JButton cancelButton = new JButton(createDropUserAction("Drop user"));

        JButton[] btn = {okButton, cancelButton};

        GUITools.makeSameSize(btn);
        p.add(okButton);
        p.add(Box.createHorizontalStrut(GUITools.STD_STRUT));
        p.add(cancelButton);

        return p;
    }

    private class UserChangedHandler implements GuiEventListener<UserChangedEvent> {
        @Override
        public void on(UserChangedEvent event) {
            User user = event.getUser();
            int selected = -1;
            switch (event.getOperation()) {
                case Create:
                    selected = tableModel.getRowCount();
                    tableModel.addUser(user);
                    break;
                case Delete:
                    int userIndex = tableModel.getUserIndex(user);
                    tableModel.deleteUser(user);
                    selected = GUITools.calculateNextSelected(tableModel.getRowCount(), userIndex);
                    break;
                case Select: case Clear:
                default:
            }
            GUITools.initTableColumnWidth(usersTable, new int[][] {{0, 40}, {1, 70}, {2, 30}}, 5, getFrameWidth());
            usersTable.repaint();
            usersTable.getSelectionModel().setSelectionInterval(0, selected);
        }
    }

    private Action createAddUserAction(String name) {
        return new AbstractAction(name) {
            private static final long serialVersionUID = 3627983760141638121L;
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info("Add user action started");
                AddUserPanel addUserPanel = new AddUserPanel();
                windowProvider.openDialog("Add new user", addUserPanel);
            }
        };
    }

    private Action createDropUserAction(String name) {
        return new AbstractAction(name) {
            private static final long serialVersionUID = -5553290349871953979L;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tableModel.isEmpty()) {
                    return;
                }
                logger.info("Drop user action started");
                User user = tableModel.getElementAt(usersTable.getSelectedRow());
                userManager.deleteUser(user);
                eventBus.post(new UserChangedEvent(user, Operation.Delete));
            }
        };
    }

}
