package com.test.app.todolist.gui.util.controls;

import com.test.app.todolist.gui.util.GUITools;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.FocusListener;

/**
 * ControlBuilder - Utility class for building swing controls
 *
 * @author Kontcur Alex (bona)
 * @since 11.07.11
 */
public class ControlBuilder {

    public static JTextField createSmartTextField(DocumentListener docListener, FocusListener focusListener,
                                                  DocumentFilter documentFilter, String startValue, int width) {
        JTextField field = new JTextField();
        AbstractDocument document = (AbstractDocument) field.getDocument();
        document.setDocumentFilter(documentFilter);
        document.addDocumentListener(docListener);

        field.addFocusListener(focusListener);
        field.setText(startValue);

        Dimension dim = new Dimension(width, field.getPreferredSize().height);
        GUITools.setFixedSize(field, dim);

        return field;
    }

    public static JTextField createSmartNumField(DocumentListener docListener, String startValue,
                                                 int maxChars, int width) {

        FieldFocusListener focusListener = new FieldFocusListener(startValue);
        FixedSizeNumFilter documentSizeFilter = new FixedSizeNumFilter(maxChars);

        return createSmartTextField(docListener, focusListener, documentSizeFilter, startValue, width);    
    }

    private ControlBuilder() {
    }

}
