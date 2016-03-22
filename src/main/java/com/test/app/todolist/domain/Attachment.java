package com.test.app.todolist.domain;

import java.io.Serializable;

/**
 * Attachment
 *
 * @author Kontcur Alex (bona)
 * @since 25.11.13
 */
public class Attachment implements Serializable {

    private static final long serialVersionUID = -7291254274754342114L;

    private long id;
    private Todo todo;
    private String content;
    private String fileName;

    protected Attachment() {
    }

    public Attachment(String fileName, String content) {
        this.fileName = fileName;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Todo getTodo() {
        return todo;
    }

    public void setTodo(Todo todo) {
        this.todo = todo;
    }

    @Override
    public String toString() {
        return fileName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Attachment attachment = (Attachment) obj;
        if (id != attachment.id) {
            return false;
        }
        if (content != null ? !content.equals(attachment.content) : attachment.content != null) {
            return false;
        }
        if (fileName != null ? !fileName.equals(attachment.fileName) : attachment.fileName != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }
}
