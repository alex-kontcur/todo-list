package com.test.app.todolist.gui.util.controls;

import javax.swing.text.DocumentFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.Toolkit;

/**
 * FixedSizeFilter - FixedSizeFilter for text field that contains common strings
 *
 * @author Kontcur Alex (bona)
 * @since 11.07.11
 */
public class FixedSizeFilter extends DocumentFilter {

    protected final int maxCharacters;

    public FixedSizeFilter(int maxCharacters) {
        this.maxCharacters = maxCharacters;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
        throws BadLocationException {

        Document document = fb.getDocument();
        String prevText = document.getText(0, document.getLength());

        if (validConditionInsert(document, text, offset)) {
            super.insertString(fb, offset, text, attr);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
        throws BadLocationException {

        Document document = fb.getDocument();
        String prevText = document.getText(0, document.getLength());

        if (validConditionReplace(document, text, offset, length)) {
            super.replace(fb, offset, length, text, attrs);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    protected boolean validConditionInsert(Document doc, String text, int offset) throws BadLocationException {
        return (doc.getLength() + text.length()) <= maxCharacters;
    }

    protected boolean validConditionReplace(Document doc, String text, int offset, int length)
        throws BadLocationException {

        return (doc.getLength() + text.length() - length) <= maxCharacters;
    }

}
