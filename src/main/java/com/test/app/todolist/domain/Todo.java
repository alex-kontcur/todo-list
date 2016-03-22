package com.test.app.todolist.domain;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Todo
 *
 * @author Kontcur Alex (bona)
 * @since 25.11.13
 */
@SuppressWarnings({"ReturnOfCollectionOrArrayField", "AssignmentToCollectionOrArrayFieldFromParameter"})
public class Todo implements Serializable, Comparable<Todo> {

    private static final long serialVersionUID = -8995703543835312514L;

    private long id;
    private User user;
    private boolean done;
    private String subject;
    private String description;

    private Set<User> watchers;
    private Set<Comment> comments;
    private Set<Attachment> attachments;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createDate;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime startDate;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime stopDate;

    @Transient
    private boolean busy;


    protected Todo() {
    }

    public Todo(String subject, String description, DateTime startDate, User user, Collection<User> watchers, boolean done) {
        this.subject = subject;
        this.description = description;
        this.startDate = startDate;
        this.user = user;
        this.done = done;

        this.watchers = new HashSet<>();
        comments = new HashSet<>();
        attachments = new HashSet<>();

        setWatchers(watchers);

        createDate = new DateTime();
    }

    public void setWatchers(Collection<User> watchers) {
        if (watchers != null) {
            this.watchers.clear();
            for (User watcher : watchers) {
                addWatcher(watcher);
            }
        }
    }

    public void clearWatchers() {
        for (User user : new ArrayList<>(watchers)) {
            removeWatcher(user);
        }
    }

    public void addWatcher(User user) {
        watchers.add(user);
        user.addWatchingTodo(this);
    }

    public void removeWatcher(User user) {
        watchers.remove(user);
        user.removeWatchingTodo(this);
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    @Override
    @SuppressWarnings("CompareToUsesNonFinalVariable")
    public int compareTo(Todo o) {
        return startDate.compareTo(o.startDate);
    }

    public long getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
        stopDate = done ? new DateTime() : null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(DateTime createDate) {
        this.createDate = createDate;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getStopDate() {
        return stopDate == null ? null : stopDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Collection<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<Attachment> attachments) {
        this.attachments = attachments;
    }

    public Collection<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
        attachment.setTodo(this);
    }

    public void removeAttachment(Attachment attachment) {
        attachments.remove(attachment);
    }

    public Collection<User> getWatchers() {
        return watchers;
    }

    public Object[] getWatchersArray() {
        return watchers.toArray();
    }

    public Object[] getAttachmentsArray() {
        return attachments.toArray();
    }

    @Override
    public String toString() {
        return "Todo [" + subject + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Todo todo = (Todo) obj;
        return id == todo.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
