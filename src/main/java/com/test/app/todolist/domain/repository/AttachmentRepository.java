package com.test.app.todolist.domain.repository;

import com.test.app.todolist.domain.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * AttachmentRepository
 *
 * @author Kontcur Alex (bona)
 * @since 25.11.13
 */
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
