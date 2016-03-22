package com.test.app.todolist.gui.provider;

import com.test.app.todolist.gui.GuiContext;
import com.test.app.todolist.gui.panels.DialogPanel;
import com.test.app.todolist.gui.panels.TodoListPanel;
import com.test.app.todolist.gui.actions.AboutAction;
import com.test.app.todolist.gui.actions.CloseAction;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * WindowProvider - * Utility singletone class for providing swing frames and dialogs
 *
 * @author Kontcur Alex (bona)
 * @since 11.07.11
 */
@Component
public class WindowProvider {

    private static final String FILE_MENU = "File";
    private static final String HELP_MENU = "Help";

    private CloseAction closeAction;

    public void openFrame(String title, TodoListPanel panel) {
        JFrame frame = new JFrame();
        frame.setTitle(title);

        closeAction = new CloseAction(frame, panel.getCloseWaitingPanel());

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeAction.actionPerformed(null);
            }
        });

        int w = panel.getFrameWidth();
        int h = panel.getFrameHeight();

        JMenuBar menuBar = createMenu(frame, panel);
        frame.setJMenuBar(menuBar);

        Container contentPane = frame.getContentPane();
        contentPane.removeAll();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(panel, BorderLayout.CENTER);
        contentPane.add(new StatusPanel(GuiContext.get().getCurrentUser().getName()), BorderLayout.PAGE_END);

        frame.setBounds(getWindowBounds(w, h));
        frame.setMinimumSize(new Dimension(w, h));

        frame.setVisible(true);
    }

    private JMenuBar createMenu(JFrame frame, TodoListPanel panel) {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu(FILE_MENU);

        JMenuItem quitMenuItem = new JMenuItem(closeAction);
        fileMenu.add(quitMenuItem);

        menuBar.add(fileMenu);

        JMenu helpMenu = new JMenu(HELP_MENU);
        JMenuItem aboutMenuItem = new JMenuItem(new AboutAction());
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        return menuBar;
    }    

    public void openDialog(String title, DialogPanel panel) {
        JDialog dialog = new JDialog(new JFrame(), title, true);
        dialog.getContentPane().add(panel);
        panel.setDialog(dialog);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        int w = panel.getDialogWidth();
        int h = panel.getDialogHeight();
        dialog.setBounds(getWindowBounds(w, h));
        dialog.setMinimumSize(new Dimension(w, h));
        dialog.setResizable(panel.resizable());
        dialog.setVisible(true);
    }

    private static Rectangle getWindowBounds(int width, int height) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = new Dimension(width, height);
        windowSize.width = Math.min(screenSize.width, windowSize.width);
        windowSize.height = Math.min(screenSize.height, windowSize.height);

        int x = Math.abs(screenSize.width - windowSize.width) / 2;
        int y = Math.abs(screenSize.height - windowSize.height) / 2;

        return new Rectangle(new Point(x, y), windowSize);
    }

    private static class StatusPanel extends JPanel {

        private final JLabel userLabel = new JLabel();
        private final JPanel loginDataPanel;

        StatusPanel(String user) {
            setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
            setLayout(new BorderLayout());

            userLabel.setHorizontalAlignment(SwingConstants.RIGHT);

            loginDataPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
            loginDataPanel.add(userLabel);

            add(loginDataPanel, BorderLayout.PAGE_END);

            setUser(user);
        }

        private void setUser(String user) {
            userLabel.setText("Logged user : " + user);
        }
    }

}
