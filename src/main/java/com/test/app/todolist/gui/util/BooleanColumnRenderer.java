package com.test.app.todolist.gui.util;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;

/**
 * BooleanColumnRenderer - Cell renderer for boolean values
 *
 * @author Kontcur Alex (bona)
 * @since 11.07.11
 */
public class BooleanColumnRenderer extends JCheckBox implements TableCellRenderer {

    private final Color color;

    public BooleanColumnRenderer(Color color) {
        setHorizontalAlignment(CENTER);
        this.color = color;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column){
        setEnabled(false);
        if (color == null) {
            setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        } else {
            setForeground(color);
            setBackground(color);
        }
        setSelected(value != null && (Boolean) value);
        return this;
    }
}
