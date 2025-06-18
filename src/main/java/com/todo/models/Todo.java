package com.todo.models;

import java.util.Objects;

public class Todo {
    private long id;
    private String text;
    private boolean completed;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Todo() {

    }

    public Todo(long id, String text, boolean completed) {
        this.id = id;
        this.text = text;
        this.completed = completed;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Todo todo = (Todo) o;
        return id == todo.id && completed == todo.completed && Objects.equals(text, todo.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, completed);
    }
}
