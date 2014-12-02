package ru.gtncraft.mongoauth.database;

public class Account {
    private final String login;
    private final long ip;
    private final boolean allowed;
    private String password;

    public Account(String login, long ip, String password, boolean allowed) {
        this.login = login;
        this.ip = ip;
        this.allowed = allowed;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public long getIp() {
        return ip;
    }

    public boolean isAllowed() {
        return allowed;
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
