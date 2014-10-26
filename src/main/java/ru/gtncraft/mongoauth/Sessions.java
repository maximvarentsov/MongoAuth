package ru.gtncraft.mongoauth;

import org.bukkit.Bukkit;
import ru.gtncraft.mongoauth.database.Account;
import ru.gtncraft.mongoauth.database.Database;

import java.io.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class Sessions implements Iterable<Session> {
    private final Map<UUID, Session> sessions = new ConcurrentHashMap<>();
    private final Collection<UUID> logged = new ConcurrentSkipListSet<>();
    private final Database database;
    private final File file;
    private final int maxPerIp;

    public Sessions(MongoAuth plugin) {
        database = plugin.getDB();
        file = new File(plugin.getDataFolder(), "sessions.dat");
        maxPerIp = plugin.getConfig().getInt("maxPerIp", 1);
    }

    public Session join(UUID player) {
        Account account = database.getAccount(player);
        Session session = new Session(account);
        sessions.put(player, session);
        return session;
    }

    public void login(UUID player) {
        logged.add(player);
    }

    public boolean quit(UUID player) {
        sessions.remove(player);
        return logged.remove(player);
    }

    public boolean notAuthenticated(UUID player) {
        return ! logged.contains(player);
    }

    public Session get(UUID player) {
        return sessions.get(player);
    }

    public boolean checkRegistrationLimit(long ip) {
        return (database.countIp(ip) + 1) > maxPerIp;
    }

    public void save() throws IOException {
        if (sessions.isEmpty()) {
            return;
        }
        if (file.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                try (ObjectOutputStream os = new ObjectOutputStream(fos)) {
                    os.writeObject(sessions);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void restore() throws IOException {
        if (!file.exists()) {
            return;
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            try (ObjectInputStream ois = new ObjectInputStream(fis)) {
                for (Map.Entry<UUID, Session> entry : ((Map<UUID, Session>) ois.readObject()).entrySet()) {
                    // TODO: check another servers
                    UUID player = entry.getKey();
                    Session session = entry.getValue();
                    if (Bukkit.getPlayer(player) != null) {
                        sessions.put(player, session);
                    }
                }
            }
        } catch (ClassNotFoundException ignore) {
        } finally {
            file.delete();
        }
    }

    @Override
    public Iterator<Session> iterator() {
        return sessions.values().iterator();
    }
}
