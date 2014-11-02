package ru.gtncraft.mongoauth;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import ru.gtncraft.mongoauth.tasks.AuthMessage;

import java.util.UUID;

class Listeners implements Listener {
	private final MongoAuth plugin;
    private final Sessions sessions;
    private final boolean spawn;
    private final boolean silentQuitJoin;

	public Listeners(final MongoAuth instance) {
        Bukkit.getServer().getPluginManager().registerEvents(this, instance);
        plugin = instance;
        sessions = instance.getSessions();
        spawn = plugin.getConfig().getBoolean("spawn", true);
        silentQuitJoin = plugin.getConfig().getBoolean("silentQuitJoin", true);
	}

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        Session session = sessions.join(id);

        if (sessions.isGuest(id)) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new AuthMessage(plugin, player));
        }

        if (spawn) {
            player.teleport(event.getPlayer().getWorld().getSpawnLocation());
        }

        if (silentQuitJoin) {
            event.setJoinMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onQuit(final PlayerQuitEvent event) {
        if (silentQuitJoin) {
            event.setQuitMessage(null);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().substring(1);
        String rootCommand = command.split(" ")[0];
        if (plugin.getCommand(rootCommand) != null && !plugin.getCommand(rootCommand).equals(plugin.getCommand("mongoauth"))) {
            return;
        }
        if (sessions.isGuest(player.getUniqueId())) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new AuthMessage(plugin, player));
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onChat(final AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (sessions.isGuest(player.getUniqueId())) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new AuthMessage(plugin, player));
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onMove(final PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (sessions.isGuest(player.getUniqueId())) {
            Location from = event.getFrom();
            Location to = event.getTo();
            if (to.getX() != from.getX() || to.getY() > from.getY() || to.getZ() != from.getZ()) {
                player.teleport(from);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onEntityDamage(final EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            UUID player = entity.getUniqueId();
            if (sessions.isGuest(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onInventoryInteract(final InventoryClickEvent event) {
        if (sessions.isGuest(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onItemDrop(final PlayerDropItemEvent event) {
		if (sessions.isGuest(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
	}

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onInteract(final PlayerInteractEvent event) {
		if (sessions.isGuest(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
	}

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onEntityInteract(final PlayerInteractEntityEvent event) {
		if (sessions.isGuest(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onPickupItem(final PlayerPickupItemEvent event) {
        if (sessions.isGuest(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onBukkitEmpty(final PlayerBucketEmptyEvent event) {
        if (sessions.isGuest(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
