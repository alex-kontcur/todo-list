package com.test.app.todolist.gui.util;

import javax.swing.*;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * BoxLayoutTools - Utility class for swing panels
 *
 * @author Kontcur Alex (bona)
 * @since 11.07.11
 */
public class BoxLayoutTools {

    /**
     * Creates vertical panel, which will contain set of components specified by the parameters.
     * Components will have same alignment and various vertical struts between them.
     *
     * @param components Array of components to be laid out
     * @param struts     Array of strut sizes between components. Should be the same size
     * @param alignment  Alignement for components being added
     * @return Panel containing arranged components
     */
    public static JPanel createVerticalComponentSet(JComponent[] components, int[] struts, float alignment) {
        JPanel panel = createVerticalPanel();
        for (int i = 0; i < components.length; i++) {
            components[i].setAlignmentX(alignment);
            panel.add(components[i]);
            panel.add(Box.createRigidArea(new Dimension(0, struts[i])));
        }
        return panel;
    }

    /**
     * Simplified version of method with the same name. Components in this method will be separated with same sized strut,
     * with no strut after the last component. Useful for the group of linked components.
     *
     * @param components JComponent
     * @param strut      int
     * @param alignment  float
     * @return JPanel
     */
    public static JPanel createVerticalComponentSet(JComponent[] components, int strut, float alignment) {
        int[] struts = new int[components.length];
        Arrays.fill(struts, strut);
        struts[struts.length - 1] = 0;
        return createVerticalComponentSet(components, struts, alignment);
    }

    /**
     * Creates horizontal panel, which will contain set of components specified by the parameters.
     * Components will have same alignment and same-sized strut between them.
     *
     * @param components Array of components to be laid out
     * @param strut      Strut size
     * @param alignment  Alignement for components being added
     * @return Panel containing arranged components
     */
    public static JPanel createHorizontalComponentSet(JComponent[] components, int strut, float alignment) {
        JPanel panel = createHorizontalPanel();
        for (int i = 0; i < components.length; i++) {
            components[i].setAlignmentY(alignment);
            panel.add(components[i]);
            // do not add last strut
            if (i != components.length - 1) {
                panel.add(Box.createHorizontalStrut(strut));
            }
        }
        return panel;
    }

    /**
     * Quickly creates panel with a vertical box layout.
     *
     * @return JPanel
     */
    public static JPanel createVerticalPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
        return p;
    }

    /**
     * Quickly creates panel with a horizontal box layout.
     *
     * @return JPanel
     */
    public static JPanel createHorizontalPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
        return p;
    }

    /**
     * Makes same alignment for the group of components.
     *
     * @param cs        JComponent[]
     * @param alignment float
     */
    public static void setGroupAlignmentX(JComponent[] cs, float alignment) {
        for (JComponent c : cs) {
            c.setAlignmentX(alignment);
        }
    }

    private static JComponent[] makeComponentsArray(Container container) {
        Component[] components = container.getComponents();
        JComponent[] jComponents = new JComponent[0];
        ArrayList<JComponent> jComponentsList = new ArrayList<>();
        for (Component component : components) {
            if (component instanceof JComponent) {
                jComponentsList.add((JComponent) component);
            }
        }
        return jComponentsList.toArray(jComponents);
    }

    /**
     * Makes same alignment for the group of components.
     *
     * @param container Container
     * @param alignment float
     */
    public static void setGroupAlignmentX(Container container, float alignment) {
        setGroupAlignmentX(makeComponentsArray(container), alignment);
    }

    /**
     * Makes same alignment for the group of components.
     *
     * @param container Container
     * @param alignment float
     */
    public static void setGroupAlignmentY(Container container, float alignment) {
        setGroupAlignmentY(makeComponentsArray(container), alignment);
    }

    /**
     * Makes same alignment for the group of components.
     *
     * @param cs        JComponent[]
     * @param alignment float
     */
    public static void setGroupAlignmentY(JComponent[] cs, float alignment) {
        for (JComponent c : cs) {
            c.setAlignmentY(alignment);
        }
    }

    private BoxLayoutTools() {
    }

}
