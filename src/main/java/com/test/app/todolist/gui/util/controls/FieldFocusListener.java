package com.test.app.todolist.gui.util.controls;

import javax.swing.*;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

/**
 * FieldFocusListener - Default realization of FocusListener
 *
 * @author Kontcur Alex (bona)
 * @since 11.07.11
 */
public class FieldFocusListener implements FocusListener {

    private final String defaultValue;

    public FieldFocusListener(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public void focusGained(FocusEvent e) {
        JTextField textField = (JTextField) e.getComponent();
        textField.selectAll();
    }

    @Override
    public void focusLost(FocusEvent e) {
        JTextField textField = (JTextField) e.getComponent();
        if (textField.getText().isEmpty()) {
            textField.setText(String.valueOf(defaultValue));
        }
    }
}
