package com.todo.requests.interfaces;

public interface CrudInterface<T> {

    Object create(T entity);

    Object update(long id, T entity);

    Object delete(long id);

}
