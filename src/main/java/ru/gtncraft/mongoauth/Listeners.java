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
import ru.gtncraft.mongoauth.manager.AuthManager;
import ru.gtncraft.mongoauth.tasks.AuthMessage;
import java.util.regex.Pattern;

class Listeners implements Listener {
	private final MongoAuth plugin;
    private final AuthManager manager;
    private final Pattern pattern;

	public Listeners(final MongoAuth instance) {
        Bukkit.getServer().getPluginManager().registerEvents(this, instance);
        plugin = instance;
        manager = instance.getAuthManager();
        pattern = Pattern.compile(plugin.getConfig().getString("general.playernamePattern"));
	}

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    void onPlayerPreLogin(final AsyncPlayerPreLoginEvent event) {
        if (!pattern.matcher(event.getName()).matches()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Messages.get(Message.error_input_invalid_login));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.teleport(player.getWorld().getSpawnLocation());
        if (!manager.isAuth(player.getUniqueId())) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new AuthMessage(plugin, player));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    @SuppressWarnings("unused")
    void onCommand(final PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().substring(1);
        String rootCommand = command.split(" ")[0];
        if (plugin.getCommand(rootCommand) != null && !plugin.getCommand(rootCommand).equals(plugin.getCommand("mongoauth"))) {
            return;
        }
        if (!manager.isAuth(player.getUniqueId())) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new AuthMessage(plugin, player));
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    @SuppressWarnings("unused")
    void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!manager.isAuth(player.getUniqueId())) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new AuthMessage(plugin, player));
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    void onPlayerMove(final PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!manager.isAuth(player.getUniqueId())) {
            Location from = event.getFrom();
            Location to = event.getTo();
            if (to.getX() != from.getX() || to.getY() > from.getY() || to.getZ() != from.getZ()) {
                player.teleport(from);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    @SuppressWarnings("unused")
    void onEntityDamage(final EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            final Player player = (Player) entity;
            if (!manager.isAuth(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    @SuppressWarnings("unused")
    void onInventoryInteract(final InventoryClickEvent event) {
        if (!manager.isAuth(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    @SuppressWarnings("unused")
    void onItemDrop(final PlayerDropItemEvent event) {
		if (!manager.isAuth(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
	}

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    @SuppressWarnings("unused")
    void onInteract(final PlayerInteractEvent event) {
		if (!manager.isAuth(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
	}

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    @SuppressWarnings("unused")
    void onEntityInteract(final PlayerInteractEntityEvent event) {
		if (!manager.isAuth(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    @SuppressWarnings("unused")
    void onPlayerPickupItem(final PlayerPickupItemEvent event) {
        if (!manager.isAuth(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
