package ru.gtncraft.mongoauth;

import java.util.UUID;

public class Account {
    private UUID id;
    private long ip;
    private boolean allowed;
    private String password;

    public Account(UUID id, long ip, String password) {
        this(id, ip, password, true);
    }

    public Account(UUID id, long ip, String password, boolean allowed) {
        this.id = id;
        this.ip = ip;
        this.allowed = allowed;
        this.password = password;
    }

    public UUID getId() {
        return id;
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
        return id.toString();
    }
}
