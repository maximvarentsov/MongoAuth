package ru.gtncraft.mongoauth;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.database.Database;
import ru.gtncraft.mongoauth.database.MongoDB;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class AuthManager {

    private final Set<String> sessions;
    private final Map<String, Location> locations;
    private Database db;
    private final File file;
    private final int maxPerIp;

    public AuthManager(final MongoAuth plugin) {
        this.locations = new ConcurrentHashMap<>();
        this.sessions = new ConcurrentSkipListSet<>();
        this.file = new File(plugin.getDataFolder() + File.separator + "sessions.dat");
        this.maxPerIp = plugin.getConfig().getInt("general.maxPerIp");

        try {
            this.db = new MongoDB(plugin);
        } catch (IOException ex) {
            plugin.getLogger().severe(ex.getMessage());
        }

        if (plugin.getConfig().getBoolean("general.restoreSessions")) {
            this.restoreSession();
        }
    }
    /**
     * Save player location and teleport to spawn.
     * @param player Player
     */
    public void saveLocation(final Player player) {
        locations.put(player.getName().toLowerCase(), player.getLocation().clone());
        final Location spawn = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
        // Check spawn location has block under player.
        if (spawn.getBlock().getType() == Material.AIR) {
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY(),spawn.getBlockZ()).setType(Material.BEDROCK);
        }
        player.teleport(spawn);
    }
    /**
     * Restore last login location.
     * @param player
     */
    public void restoreLocation(final Player player) {
        final Location location = locations.remove(player.getName().toLowerCase());
        if (location != null) {
            player.teleport(location);
        }
    }
    /**
     * Restore player sessions.
     */
    private void restoreSession() {
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
    /**
     * Save player sessions.
     */
    public void disable() {
        try {
            file.createNewFile();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                new ObjectOutputStream(fos).writeObject(sessions);
            }
        } catch (IOException ex) {}
    }
    /**
     * Get Player Account.
     */
    public Account get(final String playername) {
        return db.get(playername);
    }
    /**
     * Remove player account.
     */
    public void unregister(final Account account) {
        db.remove(account);
    }
    /**
     * Create/Update player account.
     */
    public void save(final Account account) {
        db.save(account);
    }
    /**
     * Check player is authenticated.
     */
    public boolean isAuth(final String o) {
        return sessions.contains(o.toLowerCase());
    }
    /**
     * Create player session.
     */
    public void login(final String e) {
        sessions.add(e.toLowerCase());
    }
    /**
     * Destroy player session.
     */
    public boolean logout(final String o) {
        return sessions.remove(o.toLowerCase());
    }
    /**
     * Check maximum registration per IP.
     */
    public boolean checkRegisterLimit(final Account account) {
        final int count = db.countIp(account.getIP());
        return maxPerIp < count;
    }
}
