package ru.gtncraft.mongoauth.manager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import ru.gtncraft.mongoauth.Account;
import ru.gtncraft.mongoauth.MongoAuth;
import ru.gtncraft.mongoauth.Database;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

public class AuthManager implements PluginMessageListener {

    private final Sessions sessions;
    private final Logger log;
    private final Database db;
    private final File file;
    private final int maxPerIp;

    public AuthManager(final MongoAuth plugin) throws IOException {
        this.log = plugin.getLogger();
        this.sessions = new Sessions();
        this.file = new File(plugin.getDataFolder() + File.separator + "sessions.dat");
        this.maxPerIp = plugin.getConfig().getInt("general.maxPerIp", 1);
        this.db = new Database(plugin);
        if (plugin.getConfig().getBoolean("general.restoreSessions", false)) {
            sessions.load(this.file);
        }
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(plugin, plugin.channel, this);
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
            sessions.save(file);
        } catch (IOException ex) {
            log.warning(ex.getMessage());
        }

        try {
            db.close();
        } catch (Exception ex) {
            log.warning(ex.getMessage());
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
}
