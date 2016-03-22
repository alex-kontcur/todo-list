package com.test.app.todolist.gui.util;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.util.Enumeration;

/**
 * GUITools - Utility class for swing panels
 *
 * @author Kontcur Alex (bona)
 * @since 11.07.11
 */
public class GUITools {

    /**
     * Java Look And Feel Big Strut Size.
     */
    public static final int BIG_STRUT = 17;

    /**
     * Java Look And Feel Standart Strut Size.
     */
    public static final int STD_STRUT = 12;

    /**
     * Java Look And Feel Small Strut Size.
     */
    public static final int SMALL_STRUT = 5;


    public static int calculateNextSelected(int rowCount, int userIndex) {
        if (rowCount == 1) {
            return 0;
        } else {
            if (userIndex == rowCount) {
                return userIndex - 1;
            } else if (userIndex == 0) {
                return 0;
            } else {
                return userIndex;
            }
        }
    }

    public static void makeSameSize(JComponent... components) {
        makeSameSize(components, "cancel");    
    }

    public static void makeSameSize(JComponent[] components, String minPhrase) {
        JButton minButton = new JButton(minPhrase);
        int minWidth = minButton.getPreferredSize().width;
        int[] sizes = new int[components.length];
        for (int i = 0; i < sizes.length; i++) {
            sizes[i] = components[i].getPreferredSize().width;
        }
        // find out maximum component
        int maxSizePos = maximumElementPosition(sizes);
        Dimension maxSize =
                components[maxSizePos].getPreferredSize();
        if (maxSize.width < minWidth) {
            maxSize = new Dimension(minWidth, maxSize.height);
        }
        // make all same size
        for (JComponent component : components) {
            setFixedSize(component, maxSize);
        }
    }

    /**
     * Makes a group of components same height.
     *
     * @param height
     * @param components
     */
    public static void makeSameHeight(double height, JComponent... components) {
        // make all same size
        for (JComponent component : components) {
            Dimension dim = component.getMaximumSize();
            dim.setSize(dim.getWidth(), height);
            component.setMaximumSize(dim);
        }
    }

    /**
     * Fixes problem with text field maximum size height.
     * Makes its same as a preferred size
     *
     * @param component JComponent
     */
    public static void fixTextFieldSize(JComponent component) {
        Dimension size = component.getPreferredSize();
        size.width = component.getMaximumSize().width;
        component.setMaximumSize(size);
    }

    /**
     * Fix problem with maximum height of combo box - changes to preferred height.
     *
     * @param comboBox JComboBox
     */
    public static void fixComboBoxSize(JComboBox comboBox) {
        Dimension size = comboBox.getPreferredSize();
        size.width = comboBox.getMaximumSize().width;
        comboBox.setMaximumSize(size);
    }

    /**
     * Helper method, search maximum element in array.
     *
     * @param array int[]
     * @return int
     */
    private static int maximumElementPosition(int... array) {
        int maxPos = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxPos]) {
                maxPos = i;
            }
        }
        return maxPos;
    }

    public static void initTableColumnWidth(JTable table, int[][] columnIndexesSizes, int xinset, int dialogWidth) {
        TableModel model = table.getModel();
        if (model.getColumnCount() >= columnIndexesSizes.length) {
            FontMetrics fontMetrics = table.getFontMetrics(table.getFont());
            int delta = SwingUtilities.computeStringWidth(fontMetrics, "W");

            String[] columnNames = new String[model.getColumnCount()];
            for (int i = 0; i < model.getColumnCount(); i++) {
                columnNames[i] = model.getColumnName(i);
            }
            int len = columnIndexesSizes.length;
            int[] maxWidths = new int[len];
            for (int i = 0; i < len; i++) {
                columnNames[i] = model.getColumnName(columnIndexesSizes[i][0]);
                int value = SwingUtilities.computeStringWidth(fontMetrics, columnNames[i]) + delta;
                maxWidths[i] = Math.max(maxWidths[i], value);
            }

            int columnsCount = model.getColumnCount();
            Integer[] tableColumnWidths = new Integer[columnsCount];
            int sub = xinset;
            for (int i = 0; i < len; i++) {
                maxWidths[i] = Math.min(maxWidths[i], dialogWidth * 5 / 8);
                for (int j = 0; j < columnsCount; j++) {
                    if (columnIndexesSizes[i][0] == j) {
                        tableColumnWidths[j] = maxWidths[i] + columnIndexesSizes[i][1];
                        sub += tableColumnWidths[j];
                    }
                }
            }
            int remainCnt = model.getColumnCount() - columnIndexesSizes.length;
            int remainColumnWidth = (dialogWidth - sub) / (remainCnt == 0 ? 1 : remainCnt);
            for (int k = columnIndexesSizes.length; k < columnsCount; k++) {
                tableColumnWidths[k] = remainColumnWidth;
            }
            adjustTableColumns(table, columnNames, tableColumnWidths, dialogWidth * 5 / 8);
        }
    }

    public static void adjustTableColumns(JTable table, String[] names, Integer[] columnWidths, int maxColumnWidth) {
        if (names != null && columnWidths != null && columnWidths.length != names.length) {
            columnWidths = null;
        }

        int index = 0;
        Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn column = columns.nextElement();
            int width;
            if (columnWidths == null || columnWidths.length == 0) {
                String name;
                if (names != null) {
                    name = names[index];
                } else {
                    name = column.getHeaderValue().toString();
                }
                int maxWidth = findWidestCell(table, index, name, maxColumnWidth);
                width = maxWidth;
            } else {
                width = columnWidths[index];
            }
            column.setWidth(width);
            column.setPreferredWidth(width);
            index++;
        }
    }

    /**
     * Fixes component height.
     *
     * @param comp JComponent
     * @return JComponent
     */
    public static JComponent fixingComponentHeight(JComponent comp) {
        Dimension d1 = comp.getPreferredSize();
        int height = d1.height;

        //Insets ins = comp.getInsets();
        //height += ins.top + ins.bottom;

        Dimension d2 = comp.getMaximumSize();
        d2.height = height;
        comp.setMaximumSize(d2);

        d2 = comp.getMinimumSize();
        d2.height = height;
        comp.setMinimumSize(d2);
        return comp;
    }

    public static void setFixedSize(JComponent component, Dimension dim) {
        component.setPreferredSize(dim);
        component.setMinimumSize(dim);
        component.setMaximumSize(dim);
    }

    public static <TEXT extends JTextComponent> TEXT setFieldProperty(TEXT text, DocumentFilter filter) {
        if (text.getDocument() instanceof AbstractDocument) {
            ((AbstractDocument) text.getDocument()).setDocumentFilter(filter);
        }
        fixingComponentHeight(text);
        return text;
    }

    public static void requestFocusLater(final JComponent component) {
        if (component == null) {
            return;
        }
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                component.requestFocus();
            }
        });
    }

    private static int findWidestCell(JTable table, int column, String name, int maxWidth) {
        FontMetrics fontMetrics = table.getFontMetrics(table.getFont());
        int max = SwingUtilities.computeStringWidth(fontMetrics, name);
        TableModel tableModel = table.getModel();
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            Object cellObject = tableModel.getValueAt(row, column);
            if (!(cellObject instanceof Boolean || cellObject == null)) {
                int l = SwingUtilities.computeStringWidth(fontMetrics, cellObject.toString());
                max = max > l ? max : l;
            }
        }
        max += SwingUtilities.computeStringWidth(fontMetrics, "%%");
        if (max > maxWidth) {
            max = maxWidth;
        }
        return max;
    }

    private GUITools() {
    }

}
