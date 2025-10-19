package com.example.btljavafx.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LogEntry {
    private final StringProperty time;
    private final StringProperty user;
    private final StringProperty action;

    public LogEntry(String time, String user, String action) {
        this.time = new SimpleStringProperty(time);
        this.user = new SimpleStringProperty(user);
        this.action = new SimpleStringProperty(action);
    }

    public StringProperty timeProperty() { return time; }
    public StringProperty userProperty() { return user; }
    public StringProperty actionProperty() { return action; }
}
