package com.test.app.todolist.gui.panels.todo.manage;

import com.test.app.todolist.domain.Todo;
import com.test.app.todolist.domain.User;
import com.test.app.todolist.gui.panels.DialogPanel;
import com.test.app.todolist.gui.util.BoxLayoutTools;
import com.test.app.todolist.gui.util.GUITools;
import com.test.app.todolist.gui.util.controls.FixedSizeFilter;
import com.test.utils.Helper;
import com.toedter.calendar.JCalendar;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * ManageTodoPanel
 *
 * @author Kontcur Alex (bona)
 * @since 11.07.11
 */
@SuppressWarnings({"UnnecessarilyQualifiedStaticUsage", "MagicNumber", "AbstractClassExtendsConcreteClass",
    "AbstractMethodCallInConstructor"})
public abstract class ManageTodoPanel extends DialogPanel {

    private static final Logger logger = LoggerFactory.getLogger(ManageTodoPanel.class);

    public static final String BEFORE_INTERVAL_DAYS = "todo-list.before-interval.days";

    public static final int SUBJECT_MAX_LENGTH = 30;
    public static final int DESCRIPTION_MAX_LENGTH = 1024;

    private static final int WIDTH = 550;
    private static final int HEIGHT = 900;

    private final JButton addButton;
    private final JButton cancelButton;
    private final JList freeUsers = new JList();
    private final JList usedUsers = new JList();

    protected final JTextField subjectField = new JTextField(20);
    protected final JTextArea descriptionField = new JTextArea(5, 30);
    protected final JCheckBox doneBox = new JCheckBox();
    protected final JCalendar calendar = createCalendar();

    private User selectedFree;
    private User selectedUsed;

    protected List<User> freeModel;
    protected List<User> usedModel;

    private int beforeInterval;

    protected ManageTodoPanel() {
        beforeInterval = 1;
        try {
            String intervalProperty = properties.getProperty(BEFORE_INTERVAL_DAYS);
            beforeInterval = Integer.parseInt(intervalProperty);
        } catch (NumberFormatException ignored) {
        }

        addButton = new JButton(createTodoAction());
        cancelButton = new JButton(createCancelAction("Cancel"));

        freeModel = new ArrayList<>();
        usedModel = new ArrayList<>();
    }

    protected abstract AbstractAction createTodoAction();

    protected abstract void initListModels();

    protected void initControls() {
    }

    private User[] getFreeUsers() {
        User[] out = new User[freeModel.size()];
        return freeModel.toArray(out);
    }

    private User[] getUsedUsers() {
        User[] out = new User[usedModel.size()];
        return usedModel.toArray(out);
    }

    protected void initWatchers() {
        freeUsers.setListData(getFreeUsers());
        freeUsers.addListSelectionListener(createTablesSelectionListener());
        usedUsers.setListData(getUsedUsers());
        usedUsers.addListSelectionListener(createColumnsSelectionListener());
        if (freeUsers.getModel().getSize() > 0) {
            freeUsers.setSelectedIndex(0);
        }
    }

    private ListSelectionListener createTablesSelectionListener() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                selectedFree = (User) freeUsers.getSelectedValue();
            }
        };
    }

    private ListSelectionListener createColumnsSelectionListener() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                selectedUsed = (User) usedUsers.getSelectedValue();
            }
        };
    }

    protected void processBeforeDate(Todo todo) {
        DateTime dateTime = new DateTime().plusMinutes(beforeInterval);
        if (todo.getStartDate().isBefore(dateTime)) {
            todo.setBusy(true);
        }
    }

    @Override
    public boolean resizable() {
        return true;
    }

    protected void createGUI() {
        setDialogHeight(HEIGHT);
        setDialogWidth(WIDTH);

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

        enableAddButton(calendar.getDate());

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
        JComponent[] panels = {createControlsPane(), createWatchersPane()};
        for (JComponent p : panels) {
            pane.add(p);
            pane.add(Box.createVerticalStrut(GUITools.STD_STRUT));
        }
        return pane;
    }

    private JPanel createWatchersPane() {
        JPanel pane = new JPanel();
        pane.setLayout(new GridLayout(1, 3, GUITools.STD_STRUT, 0));
        JPanel[] listPanels = {createListPanel(freeUsers, "Free users"),
            createButtonsPanel(),
            createListPanel(usedUsers, "Watchers")};
        for (JPanel p : listPanels) {
            pane.add(p);
        }

        pane.setPreferredSize(calcListPreferedSize());
        return pane;
    }

    private static Dimension calcListPreferedSize() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        dim.height /= 4;
        dim.width /= 3;
        return dim;
    }

    private JPanel createButtonsPanel() {
        JPanel buttonPanel = BoxLayoutTools.createVerticalPanel();
        buttonPanel.setBorder(new EmptyBorder(15, 5, 0, 5));

        JButton assignButton = new JButton(new AbstractAction("assign user >>") {
            private static final long serialVersionUID = 8125725941186455719L;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFree == null) {
                    return;
                }
                logger.info("Assign user action started..");

                int userIndex = freeModel.indexOf(selectedFree);

                freeModel.remove(selectedFree);
                usedModel.add(selectedFree);
                freeUsers.setListData(getFreeUsers());
                usedUsers.setListData(getUsedUsers());

                int selected = GUITools.calculateNextSelected(freeModel.size(), userIndex);
                freeUsers.setSelectedIndex(selected);
            }
        });
        JButton removeButton = new JButton(new AbstractAction("<< remove user") {
            private static final long serialVersionUID = 7991574170311635191L;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedUsed == null) {
                    return;
                }
                logger.info("Remove user assignment action started..");
                int userIndex = usedModel.indexOf(selectedUsed);

                freeModel.add(selectedUsed);
                usedModel.remove(selectedUsed);
                freeUsers.setListData(getFreeUsers());
                usedUsers.setListData(getUsedUsers());

                int selected = GUITools.calculateNextSelected(usedModel.size(), userIndex);
                usedUsers.setSelectedIndex(selected);
            }
        });

        buttonPanel.add(Box.createVerticalStrut(GUITools.STD_STRUT));
        buttonPanel.add(assignButton);
        buttonPanel.add(Box.createVerticalStrut(GUITools.STD_STRUT));
        buttonPanel.add(removeButton);
        GUITools.makeSameSize(assignButton, removeButton);

        return buttonPanel;
    }

    private static JPanel createListPanel(JList list, String caption) {
        JComponent[] comps = {new JLabel(caption), new JScrollPane(list)};
        int[] struts = {GUITools.SMALL_STRUT, 0};
        return BoxLayoutTools.createVerticalComponentSet(comps, struts, Component.LEFT_ALIGNMENT);
    }

    private JScrollPane getTextArea() {
        descriptionField.setEditable(true);
        return new JScrollPane(descriptionField);
    }

    private static JCalendar createCalendar() {
        Border etchedBorder = BorderFactory.createEtchedBorder();
        Border emptyBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border compoundBorder = BorderFactory.createCompoundBorder(etchedBorder, emptyBorder);

        JCalendar calendar = new JCalendar(new Date());
        calendar.setBorder(compoundBorder);

        //calendar.setTitleFont(new Font("Serif", Font.BOLD | Font.ITALIC, 24));
        //calendar.setDayOfWeekFont(new Font("SansSerif", Font.ITALIC, 12));
        //calendar.setDayFont(new Font("SansSerif", Font.BOLD, 16));
        //calendar.setTimeFont(new Font("DialogInput", Font.PLAIN, 16));
        //calendar.setTodayFont(new Font("Dialog", Font.PLAIN, 14));

        return calendar;
    }

    private static JPanel getFlowPanel(Component... comps) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        for (Component c : comps) {
            panel.add(c);
            panel.add(Box.createHorizontalStrut(GUITools.BIG_STRUT));
        }
        return panel;
    }

    private static JPanel getGridBagPanel(JLabel labelSubj, JTextField subjectField, JLabel labelDesc,
                                          JScrollPane scrollPane, JLabel startLabel, JCalendar calendar) {
        JPanel pane = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 5, 0, 0);
        pane.add(labelSubj, c);

        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(10, 0, 0, 25);
        pane.add(subjectField, c);

        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(10, 5, 0, 10);
        pane.add(labelDesc, c);

        c.ipady = 0;
        c.weighty = 1.0;   //request any extra vertical space
        c.anchor = GridBagConstraints.CENTER; //bottom of space
        c.insets = new Insets(0, 0, 0, 25);

        c.gridx = 1;       //aligned with button 2
        c.gridy = 1;       //third row
        pane.add(scrollPane, c);

        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(10, 5, 0, 0);
        pane.add(startLabel, c);

        c.ipady = 0;
        c.weighty = 1.0;   //request any extra vertical space
        c.anchor = GridBagConstraints.CENTER; //bottom of space
        c.insets = new Insets(0, 0, 0, 25);
        c.gridx = 1;
        c.gridy = 2;
        pane.add(calendar, c);

        return pane;
    }

    private JPanel createControlsPane() {
        JPanel pane = BoxLayoutTools.createVerticalPanel();

        GUITools.setFieldProperty(subjectField, new FixedSizeFilter(SUBJECT_MAX_LENGTH));

        pane.setBorder(BorderFactory.createTitledBorder("Info"));

        JLabel labelSubj = new JLabel("Subject:");
        JLabel labelDesc = new JLabel("Description:");
        JScrollPane scrollPane = getTextArea();
        JLabel startLabel = new JLabel("<html>Start date:<br/><br/>(must be<br/>after now)");
        JComponent[] controls = {getGridBagPanel(labelSubj, subjectField, labelDesc, scrollPane, startLabel, calendar),
            getFlowPanel(new JLabel("Is done:"), doneBox),
        };

        for (JComponent c : controls) {
            pane.add(c);
        }

        TodoDocListener docListener = new TodoDocListener();
        subjectField.getDocument().addDocumentListener(docListener);
        calendar.addPropertyChangeListener(new CalendarListener());

        return pane;
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    private class TodoDocListener implements DocumentListener {
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
            enableAddButton(calendar.getDate());
        }
    }

    private void enableAddButton(Date calendarDate) {
        addButton.setEnabled(Helper.hasValue(subjectField.getText()) && calendarDate.after(new Date()));
    }

    private Action createCancelAction(String name) {
        return new AbstractAction(name) {
            private static final long serialVersionUID = 8125725941186455719L;

            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info("Cancel action started..");
                doClose();
            }
        };
    }

    protected void doClose() {
        Window window = SwingUtilities.windowForComponent(this);
        if (window != null) {
            window.dispose();
        }
    }

    private class CalendarListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String propertyName = evt.getPropertyName();
            if (propertyName.equals("calendar")) {
                GregorianCalendar calendar = (GregorianCalendar) evt.getNewValue();
                enableAddButton(calendar.getTime());
            }

        }
    }
}
