package com.todo.requests;

public interface CrudInterface {
    Object create(Object entity);
    Object update(long id, Object entity);
    Object delete(long id);
}
