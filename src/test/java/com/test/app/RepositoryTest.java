package com.test.app;

import com.test.app.todolist.domain.Attachment;
import com.test.app.todolist.domain.Comment;
import com.test.app.todolist.domain.Todo;
import com.test.app.todolist.domain.User;
import com.test.app.todolist.domain.repository.AttachmentRepository;
import com.test.app.todolist.domain.repository.CommentRepository;
import com.test.app.todolist.domain.repository.TodoRepository;
import com.test.app.todolist.domain.repository.UserRepository;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * RepositoryTest
 *
 * @author Kontcur Alex (bona)
 * @since 26.11.13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/spring/todolist-context.xml")
public class RepositoryTest {

    protected Logger logger;

    @Inject
    private AttachmentRepository attachmentRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private TodoRepository todoRepository;

    @Inject
    private CommentRepository commentRepository;

    @Before
    public void before() {
        logger = LoggerFactory.getLogger(getClass());

        attachmentRepository.deleteAll();
        commentRepository.deleteAll();
        todoRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void attachmmentAddedAndWatcherNotDeleted() {
        User user = new User("username", "passwd", true);
        user = userRepository.save(user);

        User watcher = new User("watcher", "wpass", false);
        watcher = userRepository.save(watcher);

        Todo todo = new Todo("subj", "descr", new DateTime(), user, Arrays.asList(watcher), false);
        todo = todoRepository.saveAndFlush(todo);

        Attachment attachment = new Attachment("test.txt", "content");
        todo.addAttachment(attachment);
        attachmentRepository.save(attachment);

        Todo t = todoRepository.findAll().iterator().next();
        Assert.assertEquals("Error -> attachment wasn't added", 1, t.getAttachments().size());
        attachment = t.getAttachments().iterator().next();
        Assert.assertEquals("Error -> attachment wasn't saved corectly", "content", attachment.getContent());
        Assert.assertEquals("Error -> watcher wasn't added", 1, t.getWatchers().size());

        t = todoRepository.findAll().iterator().next();
        Assert.assertEquals("Error -> attachment wasn't added", 1, t.getAttachments().size());
        Assert.assertEquals("Error -> watcher wasn't added", 1, t.getWatchers().size());
    }

    @Test
    public void entitiesCreatedLoadedDeletedCorrectly() {
        User user = new User("username", "passwd", true);
        user = userRepository.save(user);
        Todo todo = new Todo("subj", "descr", new DateTime(), user, null, false);
        todo = todoRepository.save(todo);

        Attachment attachment = new Attachment("test.txt", "content");
        todo.addAttachment(attachment);
        Attachment saved = attachmentRepository.save(attachment);

        Attachment loaded = attachmentRepository.findOne(saved.getId());
        Assert.assertEquals("Error -> attachment doesn't saved", loaded.getFileName(), attachment.getFileName());

        attachmentRepository.delete(loaded);

        todo = todoRepository.findOne(todo.getId());
        Assert.assertEquals("Error -> attachment wasn't deleted", 0, todo.getAttachments().size());

        User watcher = new User("watcher", "wpass", false);
        watcher = userRepository.save(watcher);

        todo.addWatcher(watcher);
        todo = todoRepository.save(todo);
        Assert.assertEquals("Error -> watcher wasn't added", 1, todo.getWatchers().size());

        todo.removeWatcher(watcher);
        todoRepository.save(todo);
        Assert.assertEquals("Error -> watcher user was removed", 2, userRepository.findAll().size());

        Comment comment = new Comment("Body", watcher, todo);
        commentRepository.saveAndFlush(comment);

        watcher = userRepository.findOne(watcher.getId());
        Assert.assertEquals("Error -> watcher has no comments ->", 1, watcher.getComments().size());
    }
}
