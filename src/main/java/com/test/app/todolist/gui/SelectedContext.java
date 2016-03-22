package com.test.app.todolist.gui;

import com.test.app.todolist.domain.Attachment;
import com.test.app.todolist.domain.Todo;
import com.test.app.todolist.domain.repository.AttachmentRepository;
import com.test.app.todolist.domain.repository.TodoRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * SelectedContext
 *
 * @author Kontcur Alex (bona)
 * @since 27.11.13
 */
@Component
public class SelectedContext {

    @Inject
    private TodoRepository todoRepository;

    @Inject
    private AttachmentRepository attachmentRepository;

    private Long todoId;
    private Long attachmentId;

    public void setSelectedTodo(Long id) {
        todoId = id;
    }

    public void setSelectedAttachment(Long id) {
        attachmentId = id;
    }

    public Todo getSelectedTodo() {
        return todoId != null ? todoRepository.findOne(todoId) : null;
    }

    public Attachment getSelectedAttachment() {
        return attachmentId != null ? attachmentRepository.findOne(attachmentId) : null;
    }


}
