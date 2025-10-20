package com.example.btljavafx.utils.dao;

import java.sql.*;
import java.util.List;
import com.example.btljavafx.utils.*;

public abstract class GenericDAO<T> {

    protected Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public abstract void insert(T entity);
    public abstract boolean update(T entity);
    public abstract void delete(String id);
    public abstract List<T> getAll();
    public abstract T getById(String id);
}
