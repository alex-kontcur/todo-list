package com.test.app.todolist.gui.panels;

import com.test.app.todolist.gui.util.GUITools;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * TabbedPanel - Parent panel for all panels which contains tabbedPane
 *
 * @author Kontcur Alex (bona)
 * @since 11.07.11
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
public class TabbedPanel extends TodoListPanel {

    private final ChangeListener tabsChangeListener = createTabChangeListener();
    private volatile boolean processingTab;
    private JTabbedPane tabbedPane;

    protected List<TodoListPanel> panels = new ArrayList<>();

    public TabbedPanel() {
        createUI();
    }

    protected void createUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(GUITools.STD_STRUT, GUITools.STD_STRUT, 0, GUITools.STD_STRUT));
        add(createTabbedPanel(), BorderLayout.CENTER);
    }

    private JComponent createTabbedPanel() {
        tabbedPane = new JTabbedPane();
        for (TodoListPanel p : panels) {
            tabbedPane.add(p.getCaption(), p);
        }
        tabbedPane.addChangeListener(tabsChangeListener);
        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                tabbedPane.requestFocus();
            }
        });
        return tabbedPane;
    }

    protected ChangeListener createTabChangeListener() {
        return new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (processingTab) {
                    return;
                }
                TodoListPanel selected = (TodoListPanel) tabbedPane.getSelectedComponent();
                selected.activateTab();
            }
        };
    }

    public void insertTab(int index, TodoListPanel panel) {
        processingTab = true;
        int idx = index > panels.size() || index < 0 ? panels.size() : index;
        panels.add(idx, panel);
        tabbedPane.insertTab(panel.getCaption(), null, panel, panel.getCaption(), idx);
        processingTab = false;
    }

    public TodoListPanel removeTab(int index) {
        processingTab = true;
        if (index > panels.size() || index < 0) {
            return null;
        }
        TodoListPanel ret = panels.remove(index);
        tabbedPane.removeTabAt(index);
        processingTab = false;
        return ret;
    }

    public TodoListPanel getTabPanel(int index) {
        return panels.get(index);
    }

}
