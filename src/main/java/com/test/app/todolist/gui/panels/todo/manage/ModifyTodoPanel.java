package com.test.app.todolist.gui.panels.todo.manage;

import com.test.app.todolist.domain.Todo;
import com.test.app.todolist.domain.User;
import com.test.app.todolist.gui.Operation;
import com.test.app.todolist.event.StartPrepareEvent;
import com.test.app.todolist.event.StopPrepareEvent;
import com.test.app.todolist.event.TodoChangedEvent;
import com.test.app.todolist.gui.panels.TodoType;
import com.test.utils.Helper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.List;

/**
 * ModifyTodoPanel
 *
 * @author Kontcur Alex (bona)
 * @since 11.07.11
 */
public class ModifyTodoPanel extends ManageTodoPanel  {

    private static final Logger logger = LoggerFactory.getLogger(ModifyTodoPanel.class);

    private Todo todo;

    public ModifyTodoPanel(Todo todo) {
        this.todo = todo;

        initListModels();
        createGUI();
        initControls();
        initWatchers();
    }

    @Override
    protected void initListModels() {
        List<User> list = userManager.findAllExceptLogged();
        usedModel.addAll(todo.getWatchers());
        list.removeAll(usedModel);
        freeModel.addAll(list);
    }

    @Override
    protected void initControls() {
        subjectField.setText(todo.getSubject());
        descriptionField.setText(todo.getDescription());
        doneBox.setSelected(todo.isDone());
        calendar.setDate(new Date(todo.getStartDate().getMillis()));
    }

    @Override
    protected AbstractAction createTodoAction() {
        return new ModifyTodoAction();
    }

    private class ModifyTodoAction extends AbstractAction {
        private static final long serialVersionUID = 7035194625964095924L;

        private ModifyTodoAction() {
            super("Modify");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            logger.info("Modify todo action started..");

            String subject = subjectField.getText();

            if (!Helper.hasValue(subject)) {
                showErrorDialog("Subject field is mandatory");
                return;
            }

            String description = descriptionField.getText();
            Date startDate = calendar.getDate();
            boolean done = doneBox.isSelected();

            eventBus.post(new StartPrepareEvent());
            try {
                todo.clearWatchers();
                for (User user : usedModel) {
                    todo.addWatcher(user);
                }
                todo.setSubject(subject);
                todo.setDescription(description);
                todo.setStartDate(new DateTime(startDate.getTime()));
                todo.setDone(done);
                todo = todoRepository.save(todo);
                processBeforeDate(todo);
                eventBus.post(new TodoChangedEvent(todo, TodoType.General, Operation.Modify));
            } finally {
                eventBus.post(new StopPrepareEvent());
            }
            doClose();
        }

        private void showErrorDialog(String message) {
            JOptionPane.showMessageDialog(ModifyTodoPanel.this, message, "Modify todo", JOptionPane.WARNING_MESSAGE);
        }
    }
}
