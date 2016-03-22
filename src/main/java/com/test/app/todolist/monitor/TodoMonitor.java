package com.test.app.todolist.monitor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.test.app.todolist.domain.Todo;
import com.test.app.todolist.event.StartPrepareEvent;
import com.test.app.todolist.event.StopPrepareEvent;
import com.test.app.todolist.event.TodoNotifyEvent;
import com.test.app.todolist.gui.DataContext;
import com.test.app.todolist.model.TodoManager;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * TodoMonitor
 *
 * @author Kontcur Alex (bona)
 * @since 13.07.11
 */
@Service
public class TodoMonitor {

    private static final Logger logger = LoggerFactory.getLogger(TodoMonitor.class);

    @Value("${todo-list.scan-period.min}")
    private volatile int periodMin;

    @Value("${todo-list.before-interval.days}")
    private volatile int beforeIntervalDays;

    @Inject
    private EventBus eventBus;

    @Inject
    private TodoManager todoManager;

    @Inject
    private DataContext dataContext;

    private Timer timer;
    private volatile boolean preparing;

    @PostConstruct
    public void init() {
        eventBus.register(this);
    }

    public void start() {
        logger.info("Todo monitor starting");
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        TimerTask task = new MonitorTask();
        timer.scheduleAtFixedRate(task, 0L, TimeUnit.MINUTES.toMillis(periodMin));
    }

    public void shutdown() {
        logger.info("Todo monitor shutting down");
        timer.cancel();
    }

    @Subscribe
    public void on(StartPrepareEvent event) {
        preparing = true;
    }

    @Subscribe
    public void on(StopPrepareEvent event) {
        preparing = false;
    }

    private class MonitorTask extends TimerTask {
        @Override
        public void run() {
            List<Todo> todos = new ArrayList<>();
            List<Todo> pendingTodos = todoManager.getPendingUserTodos();
            for (Todo todo : pendingTodos) {
                if (todo.isBusy()) {
                    continue;
                }
                DateTime dateTime = new DateTime().plusDays(beforeIntervalDays);
                if (todo.getStartDate().isBefore(dateTime)) {
                    todos.add(todo);
                }
            }
            if (preparing) {
                return;
            }
            if (todos.isEmpty()) {
                return;
            }
            for (Todo todo : todos) {
                todo.setBusy(true);
                dataContext.addComingTodo(todo);
            }
            eventBus.post(new TodoNotifyEvent());
        }
    }

}
