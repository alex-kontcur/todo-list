package com.test.app.todolist.gui;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.test.app.todolist.domain.User;
import com.test.app.todolist.domain.repository.UserRepository;
import com.test.app.todolist.event.AdminLoginEvent;
import com.test.app.todolist.event.LoginEvent;
import com.test.app.todolist.event.TodoNotifyEvent;
import com.test.app.todolist.gui.actions.AboutAction;
import com.test.app.todolist.gui.panels.todo.TodoPanel;
import com.test.app.todolist.gui.panels.todo.comming.CommingTodoPanel;
import com.test.app.todolist.gui.panels.todo.general.GeneralTodoPanel;
import com.test.app.todolist.gui.panels.user.LoginPanel;
import com.test.app.todolist.gui.panels.user.UserSetupPanel;
import com.test.app.todolist.gui.provider.WindowProvider;
import com.test.app.todolist.model.UserManager;
import com.test.app.todolist.monitor.TodoMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * JavaTray - Main class that provide GUI system tray feature, starts application process
 *
 * @author Kontcur Alex (bona)
 * @since 26.11.13
 */
@Component
public class JavaTray {

    private static final Logger logger = LoggerFactory.getLogger(JavaTray.class);

    private static final String MANAGE_TODO_LABEL = "Todo setup";
    private static final String USER_SETUP_LABEL = "User setup";
    private static final String ABOUT_LABEL = "About";
    private static final String QUIT_LABEL = "Quit";

    @Inject
    private UserRepository userRepository;

    @Inject
    private WindowProvider windowProvider;

    @Inject
    private UserManager userManager;

    @Inject
    private TodoMonitor todoMonitor;

    @Inject
    private EventBus eventBus;

    private volatile TrayIcon trayIcon;
    private volatile Image current;
    private volatile Image oldTodos;
    private volatile Image newTodos;

    @PostConstruct
    public void init() {
        eventBus.register(this);
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(new Runnable() {
            @Override
            public void run() {
                List<User> users = userRepository.findAll();
                if (users.isEmpty()) {
                    User admin = userManager.createUser("admin", "admin", null, true);
                    if (admin == null) {
                        showErrorDialog("Some error occured while creating user. See logs for details.", "Creating user");
                        System.exit(0);
                    }
                    eventBus.post(new AdminLoginEvent(admin));
                    processLogin();
                } else {
                    windowProvider.openDialog("Authentication", new LoginPanel());
                }
            }
        }, 1, TimeUnit.SECONDS);
    }

    private static void showErrorDialog(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

    @Subscribe
    public void on(LoginEvent event) {
        processLogin();
    }

    private void processLogin() {
        creatGui();
        todoMonitor.start();
    }

    private void creatGui() {
        try {
            if (SystemTray.isSupported()) {
                File images = new File("images");
                File[] files = images.listFiles();
                URL middle = null;
                URL duke = null;
                for (File file : files) {
                    if (file.getName().contains("init")) {
                        middle = file.toURI().toURL();
                    }
                    if (file.getName().contains("duke")) {
                        duke = file.toURI().toURL();
                    }
                }
                oldTodos = Toolkit.getDefaultToolkit().getImage(middle);
                newTodos = Toolkit.getDefaultToolkit().getImage(duke);
                current = oldTodos;
                trayIcon = new TrayIcon(current == null ? oldTodos : current, "TODO List", createPopupMenu());
                trayIcon.setImageAutoSize(true);
                trayIcon.addActionListener(new OpenTodoAction());
                SystemTray.getSystemTray().add(trayIcon);
                logger.info("Tray started");
            } else {
                logger.error("SystemTray is not supported");
                showErrorDialog("Sorry, System tray is not supported", "Error");
                System.exit(0);
            }
        } catch (Exception e) {
            logger.error("Error while setting up tray ->", e);
            System.exit(0);
        }
    }

    private PopupMenu createPopupMenu() {
        PopupMenu popupMenu = new PopupMenu("Menu");

        MenuItem menuItem = new MenuItem(MANAGE_TODO_LABEL);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info("Opening Todo setup panel");
                TodoPanel todoPanel = new GeneralTodoPanel();
                windowProvider.openFrame(MANAGE_TODO_LABEL, todoPanel);
            }
        });
        popupMenu.add(menuItem);

        if (GuiContext.get().getCurrentUser().isAdmin()) {
            menuItem = new MenuItem(USER_SETUP_LABEL);
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    logger.info("Opening User setup panel");
                    UserSetupPanel userSetupPanel = new UserSetupPanel();
                    windowProvider.openFrame(USER_SETUP_LABEL, userSetupPanel);
                }
            });
            popupMenu.add(menuItem);
        }

        popupMenu.addSeparator();
        menuItem = new MenuItem(ABOUT_LABEL);
        menuItem.addActionListener(new AboutAction());
        popupMenu.add(menuItem);        

        popupMenu.addSeparator();
        menuItem = new MenuItem(QUIT_LABEL);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logger.info("System exiting..");
                todoMonitor.shutdown();
                System.exit(0);
            }
        });
        popupMenu.add(menuItem);
        return popupMenu;
    }

    @SuppressWarnings("SynchronizedMethod")
    private synchronized void setIcon(boolean b) {
        current = b ? newTodos : oldTodos;
        trayIcon.setImage(current);
    }

    @Subscribe
    public void on(TodoNotifyEvent event) {
        setIcon(true);
    }

    private class OpenTodoAction extends AbstractAction {
        private static final long serialVersionUID = -5197438969266468783L;

        @Override
        public void actionPerformed(ActionEvent e) {
            logger.info("Opening comming todo items ..");

            if (current.equals(newTodos)) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        setIcon(false);
                        TodoPanel todoPanel = new CommingTodoPanel();
                        windowProvider.openFrame(MANAGE_TODO_LABEL, todoPanel);
                    }
                });
                thread.start();
            }
        }
    }
}
