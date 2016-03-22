package com.test.app.todolist.gui.panels.user;

import com.test.app.todolist.domain.User;
import com.test.app.todolist.event.LoginEvent;
import com.test.app.todolist.gui.panels.DialogPanel;
import com.test.app.todolist.gui.util.BoxLayoutTools;
import com.test.app.todolist.gui.util.GUITools;
import com.test.app.todolist.gui.util.SpringLayoutTools;
import com.test.app.todolist.gui.util.controls.FieldFocusListener;
import com.test.app.todolist.gui.util.controls.FixedSizeFilter;
import com.test.utils.Helper;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.prefs.Preferences;

/**
 * LoginPanel
 *
 * @author Kontcur Alex (bona)
 * @since 11.07.11
 */
@SuppressWarnings ({"AssignmentToNull", "OverlyComplexAnonymousInnerClass"})
public class LoginPanel extends DialogPanel {

    private static final Logger logger = LoggerFactory.getLogger(LoginPanel.class);

    public static final int USER_LOGIN_MAX_LENGTH = 16;
    public static final int USER_PASS_MAX_LENGTH = 16;

    private static final int WIDTH = 280;
    private static final int HEIGHT = 240;

    public static final String FSECURE_REGISTRY_NODE = "com/test/app/todolist";
    public static final String USER_REGISTRY_KEY = "user";
    public static final String PASSWORD_REGISTRY_KEY = "password";

    private final JTextField userField = new JTextField();
    private final JPasswordField passwrdField = new JPasswordField();
    private final JCheckBox saveCredentials = new JCheckBox();

    private final JButton okButton = new JButton(createLoginAction("OK"));
    private final JButton cancelButton = new JButton(createCancelAction("Cancel"));

    public LoginPanel() {
        createGUI();
    }

    private void createGUI() {
        setDialogWidth(WIDTH);
        setDialogHeight(HEIGHT);
        
        setLayout(new BorderLayout(0, GUITools.STD_STRUT));
        setBorder(BorderFactory.createEmptyBorder(GUITools.STD_STRUT, GUITools.STD_STRUT, GUITools.STD_STRUT,
            GUITools.STD_STRUT));

        add(createTopPanel(), BorderLayout.PAGE_START);
        add(createButtonsPane(), BorderLayout.PAGE_END);
    }

    @Override
    protected void adjustControls() {
        dialog.getRootPane().setDefaultButton(okButton);
    }

    private static ImageIcon getImageIcon(Class clazz, String name) {
        URL imgURL = clazz.getResource(name);
        return (imgURL == null) ? new ImageIcon() : new ImageIcon(imgURL);
    }

    private JPanel createCredentialsPane() {

        JPanel pane = BoxLayoutTools.createVerticalPanel();
        pane.setLayout(new SpringLayout());

        pane.setBorder(BorderFactory.createTitledBorder("Credentials"));
        JComponent[] controls =
        {
            new JLabel("User Name:"),
            GUITools.setFieldProperty(userField, new FixedSizeFilter(USER_LOGIN_MAX_LENGTH)),

            new JLabel("Password:"),
            GUITools.setFieldProperty(passwrdField, new FixedSizeFilter(USER_PASS_MAX_LENGTH)),
        };

        for (JComponent c : controls) {
            pane.add(c);
        }

        pane.add(new JLabel("Save Locally"));
        pane.add(saveCredentials);

        Preferences prefs = Preferences.userRoot().node(FSECURE_REGISTRY_NODE);
        String user = prefs.get(USER_REGISTRY_KEY, Helper.EMPTY_STRING);
        userField.setText(user);
        userField.addFocusListener(new FieldFocusListener(Helper.EMPTY_STRING));
        String pass = prefs.get(PASSWORD_REGISTRY_KEY, Helper.EMPTY_STRING);
        passwrdField.setText(pass);
        passwrdField.addFocusListener(new FieldFocusListener(Helper.EMPTY_STRING));
        boolean b = Helper.hasValue(user);
        saveCredentials.setSelected(b);
        if (b) {
            userField.selectAll();
            GUITools.requestFocusLater(okButton);
        }

        SpringLayoutTools.makeCompactGrid(pane, 3, 2,
            GUITools.STD_STRUT, GUITools.STD_STRUT, GUITools.SMALL_STRUT, GUITools.STD_STRUT);

        return pane;
    }

    private JPanel createButtonsPane() {
        JPanel p = BoxLayoutTools.createHorizontalPanel();
        JButton[] btn = {okButton, cancelButton};

        GUITools.makeSameSize(btn);

        p.add(Box.createHorizontalGlue());
        p.add(okButton);
        p.add(Box.createHorizontalStrut(GUITools.STD_STRUT));
        p.add(cancelButton);
        p.add(Box.createHorizontalStrut(GUITools.SMALL_STRUT));
        return p;
    }

    private JPanel createTopPanel() {
        JPanel pane = BoxLayoutTools.createVerticalPanel();
        JComponent[] panels = {createCredentialsPane()};
        for (JComponent p : panels) {
            pane.add(p);
            pane.add(Box.createVerticalStrut(GUITools.STD_STRUT));
        }
        return pane;
    }

    private Window getWindow() {
        return SwingUtilities.windowForComponent(this);
    }

    private void doClose() {
        Window window = getWindow();
        if (window != null) {
            window.dispose();
        }
    }

    @SuppressWarnings("AnonymousInnerClassWithTooManyMethods")
    private Action createLoginAction(String name) {

        return new AbstractAction(name) {
            private static final long serialVersionUID = -1681901740993463183L;

            private User activeUser;

            private boolean checkUserInputs() {
                boolean userValid = Helper.hasValue(userField.getText());
                boolean passValid = Helper.hasValue(new String(passwrdField.getPassword()));

                return userValid && passValid;
            }

            private boolean askAuthentication() {
                String userName = userField.getText();
                String userPassword = DigestUtils.md5Hex(new String(passwrdField.getPassword()));
                activeUser = userRepository.findUserByName(userName);
                return activeUser != null && activeUser.getPassword().equals(userPassword);
            }

            private void savePrefs() {
                Preferences prefs = Preferences.userRoot().node(FSECURE_REGISTRY_NODE);
                if (saveCredentials.isSelected()) {
                    prefs.put(USER_REGISTRY_KEY, userField.getText());
                    prefs.put(PASSWORD_REGISTRY_KEY, new String(passwrdField.getPassword()));
                } else {
                    prefs.put(USER_REGISTRY_KEY, Helper.EMPTY_STRING);
                    prefs.put(PASSWORD_REGISTRY_KEY, Helper.EMPTY_STRING);
                }
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info("");
                logger.info("Login action started..");

                if (checkUserInputs() && askAuthentication()) {
                    savePrefs();
                    eventBus.post(new LoginEvent(activeUser));
                    doClose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Please check the user credentials.", 
                        "Login failed", JOptionPane.WARNING_MESSAGE);
                }
            }
        };
    }

    private Action createCancelAction(String name) {
        return new AbstractAction(name) {
            private static final long serialVersionUID = -4997167811578063738L;

            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info("Cancel login action started..");
                doClose();
                System.exit(0);
            }
        };
    }

}
