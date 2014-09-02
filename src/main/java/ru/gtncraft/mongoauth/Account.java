package ru.gtncraft.mongoauth;

import org.bukkit.entity.Player;
import org.mongodb.ConvertibleToDocument;
import org.mongodb.Document;

import java.util.Map;
import java.util.UUID;

import static ru.gtncraft.mongoauth.util.Strings.dot2LongIP;

public class Account implements ConvertibleToDocument {
    private final UUID uuid;
    private final long ip;

    private boolean allowed;
    private String password;

    public Account(final Player player) {
        uuid = player.getUniqueId();
        ip = dot2LongIP(player.getAddress().getAddress().getHostAddress());

        setAllowed(true);
    }

    public Account(final Map<String, Object> map) {
        uuid = UUID.fromString((String) map.get("uuid"));
        ip = (long) map.get("ip");

        setPassword((String) map.get("password"));
        setAllowed((boolean) map.get("allowed"));
    }

    public UUID getUUID() {
        return uuid;
    }

    public long getIP() {
        return ip;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String value) {
        password = value;
    }

    public boolean isBlocked() {
        return ! allowed;
    }

    public void setAllowed(boolean value) {
        allowed = value;
    }

    @Override
    public String toString() {
        return uuid.toString();
    }

    @Override
    public Document toDocument() {
        Document document = new Document("uuid", uuid.toString());
        document.put("ip", ip);
        document.put("password", password);
        document.put("allowed", allowed);
        return document;
    }
}
