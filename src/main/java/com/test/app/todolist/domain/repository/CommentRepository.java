package com.test.app.todolist.domain.repository;

import com.test.app.todolist.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CommentRepository
 *
 * @author Kontcur Alex (bona)
 * @since 25.11.13
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
