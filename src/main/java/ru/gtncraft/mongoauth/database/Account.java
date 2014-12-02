package ru.gtncraft.mongoauth.database;

import org.bson.types.ObjectId;

public class Account {
    private final ObjectId id;
    private final String login;
    private final long ip;
    private String password;

    public Account(String login, long ip, String password) {
        this(new ObjectId(), login, ip, password);
    }

    public Account(ObjectId id, String login, long ip, String password) {
        this.id = id;
        this.login = login;
        this.ip = ip;
        this.password = password;
    }

    public ObjectId getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public long getIp() {
        return ip;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return getLogin();
    }
}
