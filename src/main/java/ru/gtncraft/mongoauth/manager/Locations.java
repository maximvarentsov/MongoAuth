package ru.gtncraft.mongoauth.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Locations extends AbstractMap<UUID, Location> {

    final Map<UUID, Location> values;

    Locations() {
        values = new ConcurrentHashMap<>();
    }

    @Override
    public Set<Entry<UUID, Location>> entrySet() {
        return values.entrySet();
    }

    public void back(final Player player) {
        Location location = remove(player.getUniqueId());
        if (location != null) {
            player.teleport(location);
        }
    }

    public void spawn(final Player player) {
        Location spawn = Bukkit.getServer().getWorld(player.getWorld().getName()).getSpawnLocation();
        player.teleport(center(spawn));
    }

    public void save(final Player player) {
        values.put(player.getUniqueId(), player.getLocation().clone());
    }

    Location center(final Location loc) {

        Location centred = new Location(loc.getWorld(), loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ() + 0.5);

        centred.setPitch(loc.getPitch());
        centred.setYaw(loc.getYaw());

        return centred;
    }
}
