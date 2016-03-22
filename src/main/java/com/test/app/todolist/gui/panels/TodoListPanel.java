package com.test.app.todolist.gui.panels;

import com.google.common.eventbus.EventBus;
import com.test.app.todolist.domain.repository.AttachmentRepository;
import com.test.app.todolist.domain.repository.TodoRepository;
import com.test.app.todolist.domain.repository.UserRepository;
import com.test.app.todolist.gui.DataContext;
import com.test.app.todolist.gui.GuiContext;
import com.test.app.todolist.gui.PanelCloseListener;
import com.test.app.todolist.gui.SelectedContext;
import com.test.app.todolist.gui.provider.WindowProvider;
import com.test.app.todolist.model.CommentManager;
import com.test.app.todolist.model.TodoManager;
import com.test.app.todolist.model.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

/**
 * TodoListPanel - Parent panel for all panels in application
 *
 * @author Kontcur Alex (bona)
 * @since 11.07.11
 */
public class TodoListPanel extends JPanel implements PanelCloseListener {

    private static final Logger logger = LoggerFactory.getLogger(TodoListPanel.class);

    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 800;

    protected EventBus eventBus;

    protected AttachmentRepository attachmentRepository;
    protected SelectedContext selectedContext;
    protected WindowProvider windowProvider;
    protected CommentManager commentManager;
    protected UserRepository userRepository;
    protected TodoRepository todoRepository;
    protected UserManager userManager;
    protected TodoManager todoManager;
    protected DataContext dataContext;

    protected Properties properties;

    protected int frameWidth;
    protected int frameHeight;

    protected TodoListPanel() {
        GuiContext guiContext = GuiContext.get();

        frameWidth = FRAME_WIDTH;
        frameHeight = FRAME_HEIGHT;

        attachmentRepository = guiContext.getAttachmentRepository();
        selectedContext = guiContext.getSelectedContext();
        windowProvider = guiContext.getWindowProvider();
        commentManager = guiContext.getCommentManager();
        userRepository = guiContext.getUserRepository();
        todoRepository = guiContext.getTodoRepository();
        userManager = guiContext.getUserManager();
        todoManager = guiContext.getTodoManager();
        dataContext = guiContext.getDataContext();

        eventBus = guiContext.getEventBus();
        eventBus.register(this);

        properties = guiContext.getAllProperties();
    }

    public String getCaption() {
        return "";
    }

    public void activateTab() {
    }

    public void waitCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    protected void defaultCursor() {
        setCursor(Cursor.getDefaultCursor());
    }

    public PanelCloseListener getCloseWaitingPanel() {
        return this;
    }

    @Override
    public void panelClose() {
        logger.info(getClass().getSimpleName() + " panel closing process..");
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public void setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public void setFrameHeight(int frameHeight) {
        this.frameHeight = frameHeight;
    }

}
