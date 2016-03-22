package com.test.app.todolist.gui.panels.comment;

import com.test.app.todolist.domain.Todo;
import com.test.app.todolist.gui.Operation;
import com.test.app.todolist.event.TodoChangedEvent;
import com.test.app.todolist.event.UserChangedEvent;
import com.test.app.todolist.gui.GuiContext;
import com.test.app.todolist.gui.panels.DialogPanel;
import com.test.app.todolist.gui.panels.TodoType;
import com.test.app.todolist.gui.util.BoxLayoutTools;
import com.test.app.todolist.gui.util.GUITools;
import com.test.utils.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * AddCommentPanel - Panel for creating a new comment
 *
 * @author Kontcur Alex (bona)
 * @since 13.07.11
 */
public class AddCommentPanel extends DialogPanel {

    private static final Logger logger = LoggerFactory.getLogger(AddCommentPanel.class);

    public static final int USER_LOGIN_MAX_LENGTH = 16;
    public static final int USER_PASS_MAX_LENGTH = 16;
    public static final int USER_EMAIL_MAX_LENGTH = 45;

    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    private final JButton addButton = new JButton(createCommentAction("Add"));
    private final JButton cancelButton = new JButton(createCancelAction("Cancel"));

    private final JTextArea commentArea = new JTextArea();

    public AddCommentPanel() {
        createGUI();
    }

    private void createGUI() {
        setDialogWidth(WIDTH);
        setDialogHeight(HEIGHT);

        setLayout(new BorderLayout(0, GUITools.STD_STRUT));
        setBorder(BorderFactory.createEmptyBorder(GUITools.STD_STRUT, GUITools.STD_STRUT, GUITools.STD_STRUT, GUITools.STD_STRUT));

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
        JPanel pane = new JPanel(new BorderLayout());

        JPanel flow = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JLabel commentLabel = new JLabel("Your comment:");
        flow.add(commentLabel);

        pane.add(flow, BorderLayout.PAGE_START);
        pane.add(commentArea, BorderLayout.CENTER);

        CommentDocListener docListener = new CommentDocListener();
        commentArea.getDocument().addDocumentListener(docListener);

        return pane;
    }

    private Action createCommentAction(String name) {
        return new AbstractAction(name) {
            private static final long serialVersionUID = 9019060035035591340L;
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info("Create comment action started..");

                Todo todo = selectedContext.getSelectedTodo();
                commentManager.createComment(commentArea.getText(), todo);
                boolean myTodo = todo.getUser().equals(GuiContext.get().getCurrentUser());
                TodoType type = myTodo ? TodoType.General : TodoType.Foreign;
                eventBus.post(new TodoChangedEvent(todoRepository.findOne(todo.getId()), type, Operation.Modify));
                eventBus.post(new UserChangedEvent(GuiContext.get().getCurrentUser(), Operation.Modify));
                doClose();
            }

            private void showErrorDialog(String message) {
                JOptionPane.showMessageDialog(AddCommentPanel.this, message, "Add new commet", JOptionPane.WARNING_MESSAGE);
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

    private class CommentDocListener implements DocumentListener {

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
            String commentValue = commentArea.getText();
            addButton.setEnabled(Helper.hasValue(commentValue));
        }
    }

}
