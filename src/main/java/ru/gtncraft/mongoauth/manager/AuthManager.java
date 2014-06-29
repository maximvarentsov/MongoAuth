package ru.gtncraft.mongoauth.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.Account;
import ru.gtncraft.mongoauth.MongoAuth;
import ru.gtncraft.mongoauth.database.Database;
import ru.gtncraft.mongoauth.database.MongoDB;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

public class AuthManager {

    final Sessions sessions;
    final Locations locations;
    final Tasks tasks;
    final Logger log;
    final Database db;
    final File file;
    final int maxPerIp;

    public AuthManager(final MongoAuth plugin) throws IOException {
        this.log = plugin.getLogger();

        this.sessions = new Sessions();
        this.locations = new Locations();
        this.tasks = new Tasks();

        this.file = new File(plugin.getDataFolder() + File.separator + "sessions.dat");
        this.maxPerIp = plugin.getConfig().getInt("general.maxPerIp");

        this.db = new MongoDB(plugin);

        if (plugin.getConfig().getBoolean("general.restoreSessions")) {
            sessions.load(this.file);
        }
    }

    /**
     * Save current player location and teleport to spawn point.
     *
     * @param player Player
     */
    public void join(final Player player) {
        locations.save(player);
        locations.spawn(player);
    }

    /**
     * Logout player.
     *
     * Destroy player session if player was not auth, restore location.
     *
     * @return true if exit auth player else false.
     */
    public boolean exit(final Player player) {
        UUID uuid = player.getUniqueId();
        tasks.remove(uuid);

        if (isAuth(uuid)) {
            logout(uuid);
            return true;
        }

        locations.back(player);
        return false;
    }

    /**
     * Get Player Account.
     */
    public Account get(final UUID uuid) {
        return db.get(uuid);
    }

    @Deprecated
    public Account get(final String name) {
        return db.get(name);
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
        locations.back(player);
        Bukkit.getScheduler().runTaskAsynchronously(MongoAuth.getInstance(), () -> tasks.execute(player));
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
        long count = db.countIp(account.getIP()) + 1;
        return count > maxPerIp;
    }

    /**
     * Execute task after player is logged.
     *
     */
    @SuppressWarnings("unused")
    public void schedule(final Player player, final Runnable runnable) {
        tasks.schedule(player, runnable);
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
}
