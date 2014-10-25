package ru.gtncraft.mongoauth.database;

import org.bson.types.ObjectId;

import java.util.Date;
import java.util.UUID;

public class Log {
    public static enum Status {
        CONNECT(0), DISCONNECT(1), LOGIN(2);

        private final int intRepresentation;

        private Status(int intRepresentation) {
            this.intRepresentation = intRepresentation;
        }

        public int getIntRepresentation() {
            return this.intRepresentation;
        }

        public static Status fromInt(int intRepresentation) {
            switch (intRepresentation) {
                case 0:
                    return CONNECT;
                case 1:
                    return DISCONNECT;
                case 2:
                    return LOGIN;
            }
            throw new IllegalArgumentException(intRepresentation + " is not a valid index Action");
        }
    }

    private final ObjectId id;
    private final UUID player;
    private final long ip;
    private final Status status;

    public Log(ObjectId id, UUID player, long ip, Status status) {
        this.id = id;
        this.player = player;
        this.ip = ip;
        this.status = status;
    }

    public Log(UUID player, long ip, Status status) {
        this(new ObjectId(), player, ip, status);
    }

    public ObjectId getId() {
        return id;
    }

    public Date getDate() {
        return id.getDate();
    }

    public UUID getPlayer() {
        return player;
    }

    public long getIp() {
        return ip;
    }

    public Status getStatus() {
        return status;
    }
}
