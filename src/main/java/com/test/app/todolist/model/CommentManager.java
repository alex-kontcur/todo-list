package com.test.app.todolist.model;

import com.test.app.todolist.domain.Comment;
import com.test.app.todolist.domain.Todo;
import com.test.app.todolist.domain.User;
import com.test.app.todolist.domain.repository.CommentRepository;
import com.test.app.todolist.gui.GuiContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * CommentManager
 *
 * @author Kontcur Alex (bona)
 * @since 10.07.11
 */
@Component
public class CommentManager {

    @Inject
    private CommentRepository commentRepository;

    @Transactional
    public void createComment(String body, Todo todo) {
        User currentUser = GuiContext.get().getCurrentUser();
        Comment comment = new Comment(body, currentUser, todo);
        commentRepository.save(comment);
    }

}
