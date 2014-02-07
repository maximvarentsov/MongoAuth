package ru.gtncraft.mongoauth;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Locations {

    private final Map<String, Location> locations;

    public Locations() {
        this.locations = new ConcurrentHashMap<>();
    }
    /**
     * Save player location and teleport to spawn.
     * @param player Player
     */
    public void save(final Player player) {
        locations.put(player.getName().toLowerCase(), player.getLocation().clone());
        player.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
    }
    /**
     * Restore last login location.
     * @param player
     */
    public void restore(final Player player) {
        final Location location = locations.remove(player.getName().toLowerCase());
        if (location != null) {
            player.teleport(location);
        }
    }
}