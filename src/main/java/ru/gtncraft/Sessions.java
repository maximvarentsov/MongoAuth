package ru.gtncraft;

import org.bukkit.Bukkit;

import java.io.*;
import java.util.HashSet;

public class Sessions {

    private HashSet<String> sessions = new HashSet<>();
    private File file;

    public Sessions(MongoAuth instance) {
        this.file = new File(instance.getDataFolder() + File.separator + "sessions.dat");
    }

    public void load() {
        try (FileInputStream fis = new FileInputStream(file)) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            for (String player : (HashSet<String>) ois.readObject()) {
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

    public boolean contains(String o) {
        return sessions.contains(o.toLowerCase());
    }

    synchronized public void add(String e) {
        sessions.add(e.toLowerCase());
    }

    synchronized public void remove(String o) {
        sessions.remove(o.toLowerCase());
    }
}
