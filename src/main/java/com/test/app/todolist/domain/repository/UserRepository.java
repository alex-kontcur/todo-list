package com.test.app.todolist.domain.repository;

import com.test.app.todolist.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * UserRepository
 *
 * @author Kontcur Alex (bona)
 * @since 25.11.13
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByName(String name);

}
