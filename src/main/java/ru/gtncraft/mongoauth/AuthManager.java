package ru.gtncraft.mongoauth;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import ru.gtncraft.mongoauth.Account;
import ru.gtncraft.mongoauth.MongoAuth;
import ru.gtncraft.mongoauth.Database;

import java.io.*;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Logger;

public class AuthManager implements PluginMessageListener {
    private final Set<UUID> sessions = new ConcurrentSkipListSet<>();
    private final Logger log;
    private final Database db;
    private final File file;
    private final int maxPerIp;
    private final String channel = "mongoauth";

    public AuthManager(final MongoAuth plugin) throws IOException {
        log = plugin.getLogger();
        file = new File(plugin.getDataFolder(), "sessions.dat");
        maxPerIp = plugin.getConfig().getInt("general.maxPerIp", 1);
        db = new Database(plugin);
        if (plugin.getConfig().getBoolean("general.restoreSessions", false)) {
            load(file);
        }
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(plugin, channel, this);
    }
    /**
     * Logout player.
     *
     * Destroy player session if player was not auth, restore location.
     *
     * @return true if exit auth player else false.
     */
    public boolean exit(final UUID uuid) {
        if (isAuth(uuid)) {
            logout(uuid);
            return true;
        }
        return false;
    }
    /**
     * Get Player Account.
     */
    public Account get(final UUID uuid) {
        return db.findOne(uuid);
    }
    /**
     * Remove player account.
     *
     */
    public void unregister(final Account account) {
        db.remove(account);
    }
    /**
     * Create/Update player account.
     *
     */
    public void save(final Account account) {
        db.save(account);
    }
    /**
     * Check player is authenticated.
     *
     */
    public boolean isAuth(final UUID uuid) {
        return sessions.contains(uuid);
    }
    /**
     * Create player session and restore location, run post auth tasks.
     *
     */
    public void login(final Player player) {
        sessions.add(player.getUniqueId());
    }
    /**
     * Destroy player session.
     *
     */
    public boolean logout(final UUID uuid) {
        return sessions.remove(uuid);
    }
    /**
     * Check maximum registration per IP.
     *
     */
    public boolean registrationLimitMax(final Account account) {
        long count = db.countIp(account.getIp()) + 1;
        return count > maxPerIp;
    }
    /**
     * Save player sessions.
     *
     */
    public void disable() {
        try {
            db.close();
        } catch (Exception ignore) {
        } finally {
            try {
                save(file);
            } catch (IOException ex) {
                log.warning(ex.getMessage());
            }
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] bytes) {
        UUID uuid = UUID.fromString(new String(bytes));
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        if (exit(uuid)) {
            log.info("Account " + player.getName() + " logged out.");
        }
    }

    private void save(final File file) throws IOException {
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
    private void load(final File file) throws IOException {
        if (!file.exists()) {
            return;
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            try (ObjectInputStream ois = new ObjectInputStream(fis)) {
                for (UUID uuid : (Collection<UUID>) ois.readObject()) {
                    // TODO: check another servers
                    if (Bukkit.getPlayer(uuid) != null) {
                        sessions.add(uuid);
                    }
                }
            }
        } catch (ClassNotFoundException ignore) {
        } finally {
            file.delete();
        }
    }
}
