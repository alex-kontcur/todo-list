package com.test.app.todolist.gui;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.test.app.todolist.domain.User;
import com.test.app.todolist.domain.repository.AttachmentRepository;
import com.test.app.todolist.domain.repository.TodoRepository;
import com.test.app.todolist.domain.repository.UserRepository;
import com.test.app.todolist.event.*;
import com.test.app.todolist.gui.provider.WindowProvider;
import com.test.app.todolist.model.CommentManager;
import com.test.app.todolist.model.TodoManager;
import com.test.app.todolist.model.UserManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.inject.Inject;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * GuiContext
 *
 * @author Kontcur Alex (bona)
 * @since 25.11.13
 */
@SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
@Component
public class GuiContext {

    private static GuiContext guiContext;

    private Map<Class, Map<Class, GuiEventListener>> listenersMap;

    private User currentUser;

    @Inject
    private AttachmentRepository attachmentRepository;

    @Inject
    private SelectedContext selectedContext;

    @Inject
    private CommentManager commentManager;

    @Inject
    private UserRepository userRepository;

    @Inject
    private TodoRepository todoRepository;

    @Inject
    private WindowProvider windowProvider;

    @Inject
    private TodoManager todoManager;

    @Inject
    private UserManager userManager;

    @Inject
    private DataContext dataContext;

    @Inject
    private EventBus eventBus;

    @Resource(name = "allProperties")
    private Properties allProperties;

    @PostConstruct
    public void init() {
        guiContext = this;
        eventBus.register(this);
        listenersMap = new HashMap<>();
    }

    @Subscribe
    public void on(TodoNotifyEvent event) {
        processEvent(event);
    }

    @Subscribe
    public void on(LoginEvent event) {
        currentUser = event.getUser();
        processEvent(event);
    }

    @Subscribe
    public void on(AdminLoginEvent event) {
        currentUser = event.getUser();
        processEvent(event);
    }

    @Subscribe
    public void on(TodoChangedEvent event) {
        processEvent(event);
    }

    @Subscribe
    public void on(UserChangedEvent event) {
        Operation operation = event.getOperation();
        User user = event.getUser();
        switch (operation) {
            case Modify:
                user = userRepository.save(user);
                currentUser = user;
                break;
            default:
        }
        processEvent(event);
    }

    private void processEvent(Object event) {
        Map<Class, GuiEventListener> listeners = listenersMap.get(event.getClass());
        if (listeners == null) {
            return;
        }
        for (Map.Entry<Class, GuiEventListener> entry : listeners.entrySet()) {
            entry.getValue().on(event);
        }
    }

    public void removeListener(Class panelClazz) {
        for (Map.Entry<Class, Map<Class, GuiEventListener>> entry : listenersMap.entrySet()) {
            Map<Class, GuiEventListener> listenerMap = entry.getValue();
            listenerMap.remove(panelClazz);
        }
    }

    public <T> void addListener(Class panelClazz, GuiEventListener<T> listener) {
        removeListener(panelClazz);
        Type[] genericInterfaces = listener.getClass().getGenericInterfaces();
        Class<T> clazz = (Class<T>) ((ParameterizedType) genericInterfaces[0]).getActualTypeArguments()[0];
        Map<Class, GuiEventListener> listeners = listenersMap.get(clazz);
        if (listeners == null) {
            listeners = new ConcurrentHashMap<>();
            listenersMap.put(clazz, listeners);
        }
        listeners.put(panelClazz, listener);
    }

    public static GuiContext get() {
        return guiContext;
    }

    public AttachmentRepository getAttachmentRepository() {
        return attachmentRepository;
    }

    public SelectedContext getSelectedContext() {
        return selectedContext;
    }

    public CommentManager getCommentManager() {
        return commentManager;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public TodoRepository getTodoRepository() {
        return todoRepository;
    }

    public TodoManager getTodoManager() {
        return todoManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public DataContext getDataContext() {
        return dataContext;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public WindowProvider getWindowProvider() {
        return windowProvider;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public Properties getAllProperties() {
        return allProperties;
    }
}
