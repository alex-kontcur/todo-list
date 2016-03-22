package com.test.app.todolist.gui.panels;

import javax.swing.*;

/**
 * DialogPanel - Parent panel for all dialog panels
 *
 * @author Kontcur Alex (bona)
 * @since 10.07.11
 */
public class DialogPanel extends TodoListPanel {

    protected JDialog dialog;

    protected int dialogWidth;
    protected int dialogHeight;

    public void setDialog(JDialog dialog) {
        if (dialog == null) {
            return;
        }
        this.dialog = dialog;
        adjustControls();
    }

    protected void adjustControls() {
    }

    public boolean resizable() {
        return false;
    }

    public int getDialogWidth() {
        return dialogWidth;
    }

    public void setDialogWidth(int dialogWidth) {
        this.dialogWidth = dialogWidth;
    }

    public int getDialogHeight() {
        return dialogHeight;
    }

    public void setDialogHeight(int dialogHeight) {
        this.dialogHeight = dialogHeight;
    }
}
