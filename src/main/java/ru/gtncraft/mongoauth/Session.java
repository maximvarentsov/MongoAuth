package ru.gtncraft.mongoauth;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Session implements Serializable {
    private final Date connected = new Date();
    private final UUID id;
    private int attempts = 0;

    public Session(UUID id) {
        this.id = id;
    }

    public Date getConnected() {
        return connected;
    }

    public int getAttempts() {
        attempts++;
        return attempts;
    }

    public UUID getId() {
        return id;
    }
}
