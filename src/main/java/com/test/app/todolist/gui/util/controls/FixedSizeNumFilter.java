package com.test.app.todolist.gui.util.controls;

import javax.swing.text.Document;
import javax.swing.text.BadLocationException;

/**
 * FixedSizeNumFilter
 *
 * @author Kontcur Alex (bona)
 * @since 11.07.11
 */
public class FixedSizeNumFilter extends FixedSizeFilter {

    public FixedSizeNumFilter(int maxCharacters) {
        super(maxCharacters);
    }

    @Override
    protected boolean validConditionInsert(Document doc, String text, int offset) throws BadLocationException {
        boolean parentFlag = super.validConditionInsert(doc, text, offset);
        String prevText = doc.getText(0, doc.getLength());
        return parentFlag && isValidNumber(prevText, text, offset);
    }

    @Override
    protected boolean validConditionReplace(Document doc, String text, int offset, int length)
        throws BadLocationException {

        boolean parentFlag = super.validConditionReplace(doc, text, offset, length);
        String prevText = doc.getText(0, doc.getLength());
        return parentFlag && isValidNumber(prevText, text, offset);
    }

    private static boolean isValidNumber(String prevText, String newText, int offset) {
        char ch = newText.charAt(0);
        if (ch == '0' && offset == 0) {
            return false;
        }
        try {
            String s = prevText.isEmpty() ? newText : prevText.substring(0, offset) + newText + prevText.substring(offset);
            Integer intValue = Integer.valueOf(s);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }    

}
