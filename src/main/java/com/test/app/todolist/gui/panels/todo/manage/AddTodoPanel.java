package com.test.app.todolist.gui.panels.todo.manage;

import com.test.app.todolist.domain.Todo;
import com.test.app.todolist.event.StartPrepareEvent;
import com.test.app.todolist.event.StopPrepareEvent;
import com.test.app.todolist.event.TodoChangedEvent;
import com.test.app.todolist.gui.Operation;
import com.test.app.todolist.gui.panels.TodoType;
import com.test.utils.Helper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * AddTodoPanel
 *
 * @author Kontcur Alex (bona)
 * @since 14.07.11
 */
public class AddTodoPanel extends ManageTodoPanel {

    private static final Logger logger = LoggerFactory.getLogger(AddTodoPanel.class);

    public AddTodoPanel() {
        initListModels();
        createGUI();
        initControls();
        initWatchers();
    }

    @Override
    protected void initListModels() {
        freeModel.addAll(userManager.findAllExceptLogged());
    }

    @Override
    protected AbstractAction createTodoAction() {
        return new AddTodoAction();
    }

    private class AddTodoAction extends AbstractAction {
        private static final long serialVersionUID = 7035194625964095924L;

        private AddTodoAction() {
            super("Add");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            logger.info("Add todo action started..");
            String subject = subjectField.getText();
            if (Helper.hasValue(subject)) {
                String description = descriptionField.getText();
                DateTime startDate = new DateTime(calendar.getDate().getTime());
                boolean done = doneBox.isSelected();

                eventBus.post(new StartPrepareEvent());
                try {
                    Todo todo = todoManager.createTodo(subject, description, startDate, usedModel, done);
                    processBeforeDate(todo);
                    eventBus.post(new TodoChangedEvent(todo, TodoType.General, Operation.Create));
                    doClose();
                } catch (Exception ex) {
                    logger.error("Error while adding todo ->", ex);
                    showErrorDialog("Some error occured while creating todo. See logs for details.");
                } finally {
                    eventBus.post(new StopPrepareEvent());
                }
            } else {
                showErrorDialog("Subject field is mandatory");
            }
        }

        private void showErrorDialog(String message) {
            JOptionPane.showMessageDialog(AddTodoPanel.this, message, "Add new todo", JOptionPane.WARNING_MESSAGE);
        }
    }
    
}
