package com.test.app.todolist.gui.panels.todo;

import com.test.app.todolist.domain.Attachment;
import com.test.app.todolist.domain.Comment;
import com.test.app.todolist.domain.Todo;
import com.test.app.todolist.event.TodoChangedEvent;
import com.test.app.todolist.gui.GuiContext;
import com.test.app.todolist.gui.GuiEventListener;
import com.test.app.todolist.gui.Operation;
import com.test.app.todolist.gui.PanelCloseListener;
import com.test.app.todolist.gui.panels.TabbedPanel;
import com.test.app.todolist.gui.panels.TodoListPanel;
import com.test.app.todolist.gui.panels.TodoType;
import com.test.app.todolist.gui.panels.comment.AddCommentPanel;
import com.test.app.todolist.gui.util.BoxLayoutTools;
import com.test.app.todolist.gui.util.GUITools;
import com.test.app.todolist.gui.util.SpringLayoutTools;
import com.test.utils.FileUtils;
import com.test.utils.Helper;
import com.test.utils.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.TreeSet;

/**
 * TodoPanel
 *
 * @author Kontcur Alex (bona)
 * @since 14.07.11
 */
@SuppressWarnings ({"MagicNumber", "OverlyComplexAnonymousInnerClass", "ClassWithTooManyFields", "AbstractClassExtendsConcreteClass"})
public abstract class TodoPanel extends TodoListPanel {

    private static final Logger logger = LoggerFactory.getLogger(TodoPanel.class);

    private static final Object[] EMPTY_LIST_DATA = {};

    private final JTextArea descriptionArea = new JTextArea(7, 30);
    private final JTextField createDate = new JTextField(10);
    private final JTextField statDate = new JTextField(10);
    private final JTextField stopDate = new JTextField(10);

    private final JButton addAttach = new JButton(new AddAttachAction());
    private final JButton delAttach = new JButton(new DelAttachAction());
    private final JButton addComment = new JButton(new AddCommentAction());
    private final JEditorPane commentsArea = new JEditorPane();
    private final JFileChooser fileDialog = new JFileChooser();
    private final JList watchersList = new JList();
    private final JList attachList = new JList();

    private TabbedPanel tabbedPanel;
    private String lastPath;

    protected TodoPanel() {
        setFrameHeight(900);
        setFrameWidth(800);

        GuiContext guiContext = GuiContext.get();
        guiContext.addListener(TodoPanel.class, new TodoChangedHandler());

        createGui();
    }

    @Override
    public PanelCloseListener getCloseWaitingPanel() {
        return tabbedPanel.getTabPanel(0);
    }

    protected abstract TabbedPanel createTabbedPane();

    protected void createGui() {
        setLayout(new BorderLayout());
        tabbedPanel = createTabbedPane();
        add(tabbedPanel, BorderLayout.CENTER);
        add(createShowTodoPanel(), BorderLayout.PAGE_END);
        adjustControls();
    }

    private class TodoChangedHandler implements GuiEventListener<TodoChangedEvent> {
        @Override
        public void on(TodoChangedEvent event) {
            switch (event.getOperation()) {
                case Create:
                case Delete:
                case Clear:
                    clearTodoControls();
                    break;
                case Select: case Modify:
                    setTodoControls(event.getTodo());
                    break;
            }
        }
    }

    @SuppressWarnings("MethodOnlyUsedFromInnerClass")
    private void clearTodoControls() {
        watchersList.setListData(EMPTY_LIST_DATA);
        attachList.setListData(EMPTY_LIST_DATA);

        descriptionArea.setText(Helper.EMPTY_STRING);
        createDate.setText(Helper.EMPTY_STRING);
        statDate.setText(Helper.EMPTY_STRING);
        stopDate.setText(Helper.EMPTY_STRING);
        commentsArea.setText(Helper.EMPTY_STRING);

        addAttach.setEnabled(false);
        delAttach.setEnabled(false);
        addComment.setEnabled(false);

        repaint();
    }

    private void setTodoControls(Todo todo) {
        if (todo == null) {
            clearTodoControls();
        } else {
            watchersList.setListData(todo.getWatchersArray());
            descriptionArea.setText(todo.getDescription());
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy");
            createDate.setText(formatter.print(todo.getCreateDate()));
            statDate.setText(formatter.print(todo.getStartDate()));
            DateTime stopDt = todo.getStopDate();
            stopDate.setText(stopDt == null ? Helper.EMPTY_STRING : formatter.print(stopDt));
            attachList.setListData(todo.getAttachmentsArray());

            Iterable<Comment> commentSet = new TreeSet<>(todo.getComments());
            String text = buildComments(commentSet);
            commentsArea.setText(text);
            if (text.length() > 1) {
                commentsArea.select(0, 1);
            }

            addAttach.setEnabled(true);
            delAttach.setEnabled(true);
            addComment.setEnabled(true);
        }
        repaint();
    }

    private static String buildComments(Iterable<Comment> comments) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss");
        int i = 0;
        for (Comment comment : comments) {
            String body = comment.getBody().replaceAll("\n", "<br/>");
            sb
                .append(i == 0 ? Helper.EMPTY_STRING : "<br/>")
                .append("<div style=\"background-color:#f0f0f0;\"><b>")
                .append(comment.getUser().getName())
                .append("</b> added a comment<span ><br/>[")
                .append(formatter.print(comment.getCommentDate()))
                .append("]</span>")
                .append("<div style=\"background-color:#ffffff;\"><br/>")
                .append(body)
                .append("<br/></div></div>");
            i++;
        }
        return sb.toString();
    }

    private void adjustControls() {
        GUITools.makeSameSize(new AbstractButton[] {addAttach, delAttach, addComment}, "---");

        createDate.setEditable(false);
        statDate.setEditable(false);
        stopDate.setEditable(false);

        descriptionArea.setEditable(false);
        commentsArea.setEditable(false);
    }

    private JPanel createShowTodoPanel() {
        JPanel bottom = new JPanel(new BorderLayout(0, GUITools.STD_STRUT));

        bottom.add(createNorthBottomPane(), BorderLayout.PAGE_START);
        bottom.add(createCenterBottomPane(), BorderLayout.CENTER);

        return bottom;
    }

    private static JPanel createSimplePane(JLabel label, JComponent centerComponent) {
        JPanel p = new JPanel(new BorderLayout(0, GUITools.STD_STRUT));
        JPanel f = new JPanel(new FlowLayout(FlowLayout.LEADING));
        f.add(label);

        p.add(f, BorderLayout.PAGE_START);
        p.add(centerComponent, BorderLayout.CENTER);
        return p;
    }

    private JPanel createNorthBottomPane() {
        JPanel pane = BoxLayoutTools.createHorizontalPanel();
        pane.setLayout(new SpringLayout());
        JComponent[] controls = {createWatchersPane(),
                                 createDescriptionPane(),
                                 createAttachmentsPane(),
        };
        for (JComponent c : controls) {
            pane.add(c);
        }
        SpringLayoutTools.makeCompactGrid(pane, 1, 3,
            GUITools.STD_STRUT, GUITools.STD_STRUT, GUITools.SMALL_STRUT, GUITools.STD_STRUT);

        return pane;
    }

    private JPanel createWatchersPane() {
        JScrollPane watchScrollPane = new JScrollPane(watchersList);
        GUITools.setFixedSize(watchScrollPane, new Dimension(110, watchScrollPane.getHeight()));
        return createSimplePane(new JLabel("Watchers"), watchScrollPane);
    }

    private JPanel createDescriptionPane() {
        GUITools.setFixedSize(descriptionArea, new Dimension(130, descriptionArea.getHeight()));
        descriptionArea.setBorder(new LineBorder(Color.gray, 1));
        return createSimplePane(new JLabel("Description"), descriptionArea);
    }

    private JPanel createDatesPane() {
        JPanel datesPane = BoxLayoutTools.createVerticalPanel();
        datesPane.setBorder(new EmptyBorder(5, 10, 20, 10));
        Component[] controls = {new JLabel("Dates"),
                                Box.createVerticalStrut(GUITools.BIG_STRUT * 2),
                                new JLabel("Create date:"),
                                Box.createVerticalStrut(GUITools.SMALL_STRUT),
                                createDate,
                                Box.createVerticalStrut(GUITools.STD_STRUT),
                                new JLabel("Start date:"),
                                Box.createVerticalStrut(GUITools.SMALL_STRUT),
                                statDate,
                                Box.createVerticalStrut(GUITools.STD_STRUT),
                                new JLabel("Stop date:"),
                                Box.createVerticalStrut(GUITools.SMALL_STRUT),
                                stopDate,
                                Box.createVerticalStrut(GUITools.STD_STRUT),
        };
        for (Component c : controls) {
            datesPane.add(c);
        }
        GUITools.makeSameHeight(21.0, createDate, statDate, stopDate);
        return datesPane;
    }

    private JPanel createAttachmentsPane() {
        JPanel p = new JPanel(new BorderLayout(0, GUITools.STD_STRUT));
        JPanel f = BoxLayoutTools.createVerticalPanel();
        f.setBorder(new EmptyBorder(5, 5, 5, 5));

        f.add(Box.createVerticalStrut(60));
        f.add(addAttach);
        f.add(delAttach);

        p.add(f, BorderLayout.LINE_END);
        attachList.addListSelectionListener(createAttachSelectionListener());
        attachList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && !e.isConsumed()) {
                    e.consume();
                    try {
                        Attachment attachment = selectedContext.getSelectedAttachment();
                        runTempFile(attachment.getFileName(), attachment.getContent());
                    } catch (Exception ex) {
                        logger.error("Error while running temp attachment ->", ex);
                    }
                }
            }
        });
        JScrollPane attachScrollPane = new JScrollPane(attachList);
        GUITools.setFixedSize(attachScrollPane, new Dimension(190, attachScrollPane.getHeight()));
        p.add(createSimplePane(new JLabel("Attachments"), attachScrollPane), BorderLayout.CENTER);
        return p;
    }

    private void runTempFile(String fileName, String content) {
        File tempFile = FileUtils.createTempFile("todo-attach", fileName, content);
        if (tempFile == null) {
            JOptionPane.showMessageDialog(this, "Failed to run attachment", "Run file", JOptionPane.WARNING_MESSAGE);
        }
        try {
            waitCursor();
            FileUtils.runFileCmd(tempFile.getPath());
        } catch (Exception e) {
            logger.error("Error while running attach ->", e);
            JOptionPane.showMessageDialog(this, "Failed to run attachment", "Run file", JOptionPane.WARNING_MESSAGE);
        } finally {
            defaultCursor();
        }
    }

    private JPanel createCenterBottomPane() {
        JPanel p = new JPanel(new BorderLayout(0, GUITools.STD_STRUT));
        p.setBorder(new EmptyBorder(0, 0, 15, 4));
        p.add(createDatesPane(), BorderLayout.LINE_START);
        JPanel f = BoxLayoutTools.createVerticalPanel();
        f.setBorder(new EmptyBorder(5, 5, 5, 5));
        f.add(Box.createVerticalStrut(60));
        f.add(addComment);
        p.add(f, BorderLayout.LINE_END);
        JScrollPane commentScrollPane = new JScrollPane(commentsArea);
        p.add(createSimplePane(new JLabel("Comments"), commentScrollPane), BorderLayout.CENTER);
        commentsArea.setContentType("text/html");
        commentScrollPane.setPreferredSize(new Dimension(getFrameWidth(), 220));
        commentScrollPane.setMaximumSize(new Dimension(getFrameWidth(), 220));

        return p;
    }

    private class DelAttachAction extends AbstractAction {
        private static final long serialVersionUID = 6609672274564260887L;

        DelAttachAction() {
            super("-");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Attachment attachment = selectedContext.getSelectedAttachment();
            if (attachment == null) {
                return;
            }
            logger.info("Delete attachment action started..");
            Todo todo = selectedContext.getSelectedTodo();
            todo.removeAttachment(attachment);
            attachmentRepository.delete(attachment);
            setTodoControls(todo);
        }
    }

    private class AddCommentAction extends AbstractAction {
        private static final long serialVersionUID = 1484934335656271145L;

        AddCommentAction() {
            super("+");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            logger.info("Add comment action started..");
            AddCommentPanel addCommentPanel = new AddCommentPanel();
            windowProvider.openDialog("Add new comment", addCommentPanel);
        }
    }

    //TODO - комментарии при старте не показываются
    //TODO - modify todo -> assign user -> enable add button

    private class AddAttachAction extends AbstractAction {
        private static final long serialVersionUID = -7253432366096860573L;

        AddAttachAction() {
            super("+");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            logger.info("Add attachment action started..");
            try {
                waitCursor();

                String userHome = System.getProperty("user.home");
                String dir = Helper.hasValue(lastPath) ? lastPath : userHome + File.separator;
                fileDialog.setCurrentDirectory(new File(dir));
                fileDialog.setMultiSelectionEnabled(false);
                fileDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
                boolean approve = fileDialog.showOpenDialog(TodoPanel.this) == JFileChooser.APPROVE_OPTION;
                String filePath = approve ? fileDialog.getSelectedFile().getAbsolutePath() : null;
                if (Helper.hasValue(filePath)) {
                    File file = new File(filePath);
                    lastPath = file.getPath();
                    try {
                        String content = StringUtils.fromFile(file.getAbsolutePath(), "cp1251");
                        if (content.length() > 256) {
                            content = content.substring(0, content.length());
                        }
                        Todo todo = selectedContext.getSelectedTodo();
                        Attachment attachment = new Attachment(file.getName(), content);
                        todo.addAttachment(attachment);
                        attachmentRepository.save(attachment);
                        todo = selectedContext.getSelectedTodo();
                        TodoType type = todo.getUser().equals(GuiContext.get().getCurrentUser()) ? TodoType.General : TodoType.Foreign;
                        eventBus.post(new TodoChangedEvent(todo, type, Operation.Modify));
                        setTodoControls(todo);
                    } catch (Exception ignored) {
                        showErrorDialog("Some error occured while creating attachment. See logs for details.");
                    }
                }
            } finally {
                defaultCursor();
            }
        }

        private void showErrorDialog(String message) {
            JOptionPane.showMessageDialog(
                TodoPanel.this, message, "Add new attachment", JOptionPane.WARNING_MESSAGE);
        }
    }

    private ListSelectionListener createAttachSelectionListener() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                Attachment attachment = (Attachment) attachList.getSelectedValue();
                if (attachment != null) {
                    selectedContext.setSelectedAttachment(attachment.getId());
                }
            }
        };
    }

}