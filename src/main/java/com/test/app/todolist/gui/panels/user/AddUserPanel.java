package com.test.app.todolist.gui.panels.user;

import com.test.app.todolist.domain.User;
import com.test.app.todolist.gui.Operation;
import com.test.app.todolist.event.UserChangedEvent;
import com.test.app.todolist.gui.panels.DialogPanel;
import com.test.app.todolist.gui.util.BoxLayoutTools;
import com.test.app.todolist.gui.util.GUITools;
import com.test.app.todolist.gui.util.SpringLayoutTools;
import com.test.app.todolist.gui.util.controls.FixedSizeFilter;
import com.test.utils.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * AddUserPanel - Panel for add user operation
 *
 * @author Kontcur Alex (bona)
 * @since 11.07.11
 */
public class AddUserPanel extends DialogPanel {

    private static final Logger logger = LoggerFactory.getLogger(AddUserPanel.class);

    public static final int USER_LOGIN_MAX_LENGTH = 16;
    public static final int USER_PASS_MAX_LENGTH = 16;
    public static final int USER_EMAIL_MAX_LENGTH = 45;

    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    private final JButton addButton = new JButton(createUserAction("Add"));
    private final JButton cancelButton = new JButton(createCancelAction("Cancel"));

    private final JTextField userField = new JTextField();
    private final JTextField passField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JCheckBox adminBox = new JCheckBox();

    public AddUserPanel() {
        createGUI();
    }

    private void createGUI() {
        setDialogWidth(WIDTH);
        setDialogHeight(HEIGHT);

        setLayout(new BorderLayout(0, GUITools.STD_STRUT));

        setBorder(BorderFactory.createEmptyBorder(GUITools.STD_STRUT,
            GUITools.STD_STRUT, GUITools.STD_STRUT, GUITools.STD_STRUT));

        add(createTopPanel(), BorderLayout.CENTER);
        add(createButtonsPane(), BorderLayout.PAGE_END);
    }

    @Override
    protected void adjustControls() {
        dialog.getRootPane().setDefaultButton(addButton);
    }

    private JPanel createButtonsPane() {
        JPanel p = BoxLayoutTools.createHorizontalPanel();
        JButton[] btn = {addButton, cancelButton};

        addButton.setEnabled(false);

        GUITools.makeSameSize(btn);

        p.add(Box.createHorizontalGlue());
        p.add(addButton);
        p.add(Box.createHorizontalStrut(GUITools.STD_STRUT));
        p.add(cancelButton);
        p.add(Box.createHorizontalStrut(GUITools.SMALL_STRUT));

        return p;
    }

    private JPanel createTopPanel() {
        JPanel pane = BoxLayoutTools.createVerticalPanel();
        JComponent[] panels = {createControlsPane()};
        for (JComponent p : panels) {
            pane.add(p);
            pane.add(Box.createVerticalStrut(GUITools.STD_STRUT));
        }
        return pane;
    }

    private JPanel createControlsPane() {
        JPanel pane = BoxLayoutTools.createVerticalPanel();
        pane.setLayout(new SpringLayout());

        pane.setBorder(BorderFactory.createTitledBorder("Credentials"));
        JComponent[] controls =
            {
                new JLabel("User Name:"),
                GUITools.setFieldProperty(userField, new FixedSizeFilter(USER_LOGIN_MAX_LENGTH)),

                new JLabel("Password:"),
                GUITools.setFieldProperty(passField, new FixedSizeFilter(USER_PASS_MAX_LENGTH)),

                new JLabel("Email:"),
                GUITools.setFieldProperty(emailField, new FixedSizeFilter(USER_EMAIL_MAX_LENGTH)),

                new JLabel("Is admin:"),
                adminBox,

            };

        for (JComponent c : controls) {
            pane.add(c);
        }

        UserDocListener docListener = new UserDocListener();
        userField.getDocument().addDocumentListener(docListener);
        passField.getDocument().addDocumentListener(docListener);

        SpringLayoutTools.makeCompactGrid(pane, 4, 2,
            GUITools.STD_STRUT, GUITools.STD_STRUT, GUITools.SMALL_STRUT, GUITools.STD_STRUT);

        return pane;
    }

    private Action createUserAction(String name) {
        return new AbstractAction(name) {
            private static final long serialVersionUID = 9019060035035591340L;

            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info("Add user action started..");

                String name = userField.getText();
                String pass = passField.getText();
                String email = emailField.getText();
                boolean admin = adminBox.isSelected();
                User newUser = userManager.createUser(name, pass, email, admin);

                eventBus.post(new UserChangedEvent(newUser, Operation.Create));

                doClose();
            }
        };
    }

    private Action createCancelAction(String name) {
        return new AbstractAction(name) {
            private static final long serialVersionUID = 9019060035035591340L;

            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info("Cancel action started..");
                doClose();
            }
        };
    }

    private void doClose() {
        Window window = SwingUtilities.windowForComponent(this);
        if (window != null) {
            window.dispose();
        }
    }

    private class UserDocListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            enableControl(e);
        }
        @Override
        public void removeUpdate(DocumentEvent e) {
            enableControl(e);
        }
        @Override
        public void changedUpdate(DocumentEvent e) {
            enableControl(e);
        }
        private void enableControl(DocumentEvent e) {
            String userValue = userField.getText();
            String passValue = passField.getText();
            boolean flag = Helper.hasValue(userValue) && Helper.hasValue(passValue);
            addButton.setEnabled(flag);
        }
    }

}
