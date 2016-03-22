package com.test.app.todolist.gui.panels.info;

import com.test.app.todolist.gui.panels.DialogPanel;
import com.test.app.todolist.gui.util.GUITools;

import javax.swing.*;
import java.awt.*;

/**
 * AboutPanel
 *
 * @author Kontcur Alex (bona)
 * @since 13.07.11
 */
public class AboutPanel extends DialogPanel {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    private static final String ABOUT_TEXT =
        "<div >Test desktop swing application \"TODO List\"<br/>" +
        "Created by Kontcur Alex</div>";

    public AboutPanel() {
        createGUI();
    }

    private void createGUI() {
        setDialogWidth(WIDTH);
        setDialogHeight(HEIGHT);

        setLayout(new BorderLayout(0, GUITools.STD_STRUT));

        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setBackground(new Color(150, 150, 150));
        editorPane.setContentType("text/html");
        editorPane.setText(ABOUT_TEXT);

        add(editorPane, BorderLayout.CENTER);
    }

}
