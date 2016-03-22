package com.test.app.todolist.domain;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Comment
 *
 * @author Kontcur Alex (bona)
 * @since 25.11.13
 */
public class Comment implements Serializable, Comparable<Comment> {

    private static final long serialVersionUID = -7118592869340931228L;

    private long id;
    private Todo todo;
    private User user;
    private String body;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime commentDate;

    protected Comment() {
    }

    public Comment(String body, User user, Todo todo) {
        this.body = body;
        this.user = user;
        this.todo = todo;

        commentDate = new DateTime();
    }

    @Override
    @SuppressWarnings ({"CompareToUsesNonFinalVariable", "NestedConditionalExpression"})
    public int compareTo(Comment o) {
        long fMillis = commentDate.getMillis();
        long lMillis = o.commentDate.getMillis();
        return fMillis == lMillis ? 0 : fMillis < lMillis ? 1 : -1;
    }

    public long getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Todo getTodo() {
        return todo;
    }

    public void setTodo(Todo todo) {
        this.todo = todo;
    }

    public User getUser() {
        return user;
    }

    public DateTime getCommentDate() {
        return commentDate;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Comment [" + (body.length() > 10 ? body.substring(0, 9) : body) + "... ]";
    }

    @SuppressWarnings("OverlyComplexMethod")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Comment comment = (Comment) obj;
        if (id != comment.id) {
            return false;
        }
        if (body != null ? !body.equals(comment.body) : comment.body != null) {
            return false;
        }
        if (commentDate != null ? !commentDate.equals(comment.commentDate) : comment.commentDate != null) {
            return false;
        }
        if (todo != null ? !todo.equals(comment.todo) : comment.todo != null) {
            return false;
        }
        if (user != null ? !user.equals(comment.user) : comment.user != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (todo != null ? todo.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (body != null ? body.hashCode() : 0);
        result = 31 * result + (commentDate != null ? commentDate.hashCode() : 0);
        return result;
    }
}
