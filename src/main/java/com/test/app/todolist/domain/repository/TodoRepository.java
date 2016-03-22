package com.test.app.todolist.domain.repository;

import com.test.app.todolist.domain.Todo;
import com.test.app.todolist.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TodoRepository
 *
 * @author Kontcur Alex (bona)
 * @since 25.11.13
 */
@Transactional
public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByUserAndDone(User user, boolean done);

    List<Todo> findByUser(User user);

}
