package ru.gtncraft.mongoauth;

import org.bukkit.Bukkit;

import java.io.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SessionManager {

    private final Set<String> sessions;
    private final File file;

    public SessionManager(final MongoAuth instance) {
        this.file = new File(instance.getDataFolder() + File.separator + "sessions.dat");
        this.sessions = Collections.synchronizedSet(new HashSet<String>());
    }

    public void load() {
        try (FileInputStream fis = new FileInputStream(file)) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            for (String player : (Set<String>) ois.readObject()) {
                if (Bukkit.getServer().getPlayer(player) != null) {
                    sessions.add(player);
                }
            }
            file.delete();
        } catch (IOException | ClassNotFoundException ex) {}
    }

    public void save() {
        try {
            file.createNewFile();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                new ObjectOutputStream(fos).writeObject(sessions);
            }
        } catch (IOException ex) {}
    }

    public boolean contains(final String o) {
        return sessions.contains(o.toLowerCase());
    }

    public void add(final String e) {
        sessions.add(e.toLowerCase());
    }

    public boolean remove(final String o) {
        return sessions.remove(o.toLowerCase());
    }
}
