package com.test.app.todolist.gui.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * CancelSearchAction
 *
 * @author Kontcur Alex (bona)
 * @since 13.07.11
 */
public class CancelSearchAction extends AbstractAction {

    private static final Logger logger = LoggerFactory.getLogger(CancelSearchAction.class);

    private static final long serialVersionUID = -3338908430198015227L;

    private final SearchAction searchAction;

    public CancelSearchAction(SearchAction searchAction) {
        super("Clear");
        this.searchAction = searchAction;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        logger.info("Cancel search action started..");
        searchAction.clearText();
        searchAction.actionPerformed(e);
    }
}
