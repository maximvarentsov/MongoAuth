package ru.gtncraft.mongoauth.database;

import java.util.Date;
import java.util.UUID;

public class Log {
    public static enum Status {
        CONNECT(0), DISCONNECT(1), LOGIN(2), BAD_LOGIN(3);

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
                case 3:
                    return BAD_LOGIN;
            }
            throw new IllegalArgumentException(intRepresentation + " is not a valid index Action");
        }
    }

    private final Date date;
    private final UUID player;
    private final long ip;
    private final Status status;

    public Log(Date date, UUID player, long ip, Status status) {
        this.date = date;
        this.player = player;
        this.ip = ip;
        this.status = status;
    }

    public Log(UUID player, long ip, Status status) {
        this(new Date(), player, ip, status);
    }

    public Date getDate() {
        return date;
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
