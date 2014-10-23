package ru.gtncraft.mongoauth.manager;

import org.bukkit.Bukkit;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

class Sessions extends AbstractSet<UUID> {

    private final Set<UUID> values;

    Sessions() {
        values = new ConcurrentSkipListSet<>();
    }

    @Override
    public Iterator<UUID> iterator() {
        return values.iterator();
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean add(final UUID e) {
        return values.add(e);
    }

    public void save(final File file) throws IOException {
        if (values.size() > 0) {
            try {
                if (file.createNewFile()) {
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        try (ObjectOutputStream os = new ObjectOutputStream(fos)) {
                            os.writeObject(values);
                        }
                    }
                } else {
                    throw new IOException("Could not create file " + file.getName() + ".");
                }
            } catch (IOException ex) {
                throw new IOException(ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void load(final File file) throws IOException {
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                try (ObjectInputStream ois = new ObjectInputStream(fis)) {
                    for (UUID uuid : (Collection<UUID>) ois.readObject()) {
                        if (Bukkit.getPlayer(uuid) != null) {
                            values.add(uuid);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException ex) {
                throw new IOException(ex);
            } finally {
                file.delete();
            }
        }
    }
}
