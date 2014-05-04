package ru.gtncraft.mongoauth;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.database.Database;
import ru.gtncraft.mongoauth.database.MongoDB;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class AuthManager {

    final MongoAuth plugin;
    final Collection<String> sessions;
    final Map<String, Location> locations;
    final Map<String, Collection<Runnable>> postAuth;

    Database db;
    final File file;
    final int maxPerIp;

    public AuthManager(final MongoAuth plugin) {
        this.locations = new ConcurrentHashMap<>();
        this.sessions = new ConcurrentSkipListSet<>();
        this.postAuth = new ConcurrentHashMap<>();
        this.file = new File(plugin.getDataFolder() + File.separator + "sessions.dat");
        this.maxPerIp = plugin.getConfig().getInt("general.maxPerIp");
        this.plugin = plugin;
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
     * Save player current location and teleport to spawn.
     * @param player Player
     */
    public void join(final Player player) {
        locations.put(player.getName().toLowerCase(), player.getLocation().clone());
        final Location spawn = Bukkit.getServer().getWorld(player.getWorld().getName()).getSpawnLocation();
        // Check spawn location has block under player and two air block up.
        if (spawn.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR || spawn.getBlock().getType() != Material.AIR || spawn.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR) {
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY() - 1, spawn.getBlockZ()).setType(Material.BEDROCK);
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()).setType(Material.AIR);
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY() + 1, spawn.getBlockZ()).setType(Material.AIR);
        }
        Location center = new Location(spawn.getWorld(), spawn.getBlockX() + 0.5, spawn.getBlockY(), spawn.getBlockZ() + 0.5);
        center.setPitch(spawn.getPitch());
        center.setYaw(spawn.getYaw());
        player.teleport(center);
    }
    /**
     * If player is auth, just destroy session. If not restore location.
     */
    public boolean exit(final Player player) {
        postAuth.remove(player.getName().toLowerCase());
        if (isAuth(player.getName())) {
            logout(player.getName());
            return true;
        } else {
            restoreLocation(player);
            return false;
        }
    }
    /**
     * Restore last login location.
     * @param player the player
     */
    void restoreLocation(final Player player) {
        final Location location = locations.remove(player.getName().toLowerCase());
        if (location != null) {
            player.teleport(location);
        }
    }
    /**
     * Restore player sessions.
     */
    void restoreSession() {
        try (FileInputStream fis = new FileInputStream(file)) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            ((Collection<String>) ois.readObject()).stream().filter(
                    p -> Bukkit.getServer().getPlayer(p) != null
            ).forEach(sessions::add);
            file.delete();
        } catch (IOException | ClassNotFoundException ex) {}
    }
    /**
     * Save player sessions.
     */
    public void disable() {
        if (sessions.isEmpty()) {
            return;
        }
        try {
            file.createNewFile();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                new ObjectOutputStream(fos).writeObject(sessions);
            }
        } catch (IOException ex) {}
        try {
            db.close();
        } catch (Exception ex) {}
    }
    /**
     * Get Player Account.
     */
    public Account get(final String playername) {
        return db.get(playername.toLowerCase());
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
     * Create player session and restore location, run post auth tasks.
     */
    public void login(final Player player) {
        final String playername = player.getName().toLowerCase();
        sessions.add(playername);
        restoreLocation(player);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (postAuth.containsKey(playername)) {
                Iterator<Runnable> it = postAuth.get(playername).iterator();
                while (it.hasNext()) {
                    Runnable task = it.next();
                    task.run();
                    it.remove();
                }
            }
        });
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
    public boolean registrationLimitMax(final Account account) {
        final long count = db.countIp(account.getIP()) + 1;
        return count > maxPerIp;
    }
    /**
     * Execute task after player is logged.
     */
    @SuppressWarnings("unused")
    public void addPostAuth(final Player player, final Runnable runnable) {
        final String playername = player.getName().toLowerCase();

        if (postAuth.get(playername) == null) {
            postAuth.put(playername, new ArrayList<>());
        }

        postAuth.get(playername).add(runnable);
    }
}
