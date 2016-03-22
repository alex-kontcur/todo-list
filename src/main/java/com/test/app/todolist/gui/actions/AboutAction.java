package com.test.app.todolist.gui.actions;

import com.test.app.todolist.gui.GuiContext;
import com.test.app.todolist.gui.panels.info.AboutPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * AboutAction - shows About dialog
 *
 * @author Kontcur Alex (bona)
 * @since 13.07.11
 */
public class AboutAction extends AbstractAction {

    private static final Logger logger = LoggerFactory.getLogger(AboutAction.class);

    private static final long serialVersionUID = 2741729015431319084L;

    private static final String ABOUT_LABEL = "About";

    public AboutAction() {
        super("About");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        logger.info("AboutAction starting");
        AboutPanel aboutPanel = new AboutPanel();
        GuiContext.get().getWindowProvider().openDialog(ABOUT_LABEL, aboutPanel);
    }
}