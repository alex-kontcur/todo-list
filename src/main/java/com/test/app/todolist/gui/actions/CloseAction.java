package com.test.app.todolist.gui.actions;

import com.test.app.todolist.gui.PanelCloseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * CloseAction - closes main frame
 *
 * @author Kontcur Alex (bona)
 * @since 13.07.11
 */
public class CloseAction extends AbstractAction {

    private static final Logger logger = LoggerFactory.getLogger(CloseAction.class);

    private static final long serialVersionUID = -316400380832196163L;

    private final JFrame frame;
    private final PanelCloseListener closeListener;

    public CloseAction(JFrame frame, PanelCloseListener closeListener) {
        super("Close window");
        this.frame = frame;
        this.closeListener = closeListener;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        logger.info("Close action started..");

        closeListener.panelClose();
        frame.dispose();
    }
}
