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

	final MongoAuth plugin;
	final AuthManager manager;
    final Pattern pattern;
    final Config config;

	public Listeners(final MongoAuth instance) {
        this.plugin = instance;
        this.plugin.getServer().getPluginManager().registerEvents(this, instance);
        this.manager = instance.getAuthManager();
        this.config = plugin.getConfig();
        this.pattern = Pattern.compile(this.config.getString("general.playernamePattern"));
	}

    @EventHandler()
    @SuppressWarnings("unused")
    public void onPlayerPreLogin(final AsyncPlayerPreLoginEvent event) {
        String playername = event.getName();
        if (!pattern.matcher(playername).matches()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(config.getMessage(Messages.error_input_invalid_login));
            return;
        }

        for (Player online : Bukkit.getServer().getOnlinePlayers()) {
            if (online.getName().equalsIgnoreCase(playername)) {
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage(config.getMessage(Messages.error_account_online, playername));
                return;
            }
        }
	}

    @EventHandler()
    @SuppressWarnings("unused")
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        manager.join(player);
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new AuthMessage(plugin, player));
    }

    @EventHandler()
    @SuppressWarnings("unused")
    public void onPlayerQuitEvent(final PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (manager.exit(player)) {
            plugin.getLogger().info("Account " + player.getName() + " logged out.");
        }
    }

    @EventHandler(ignoreCancelled = true)
    @SuppressWarnings("unused")
    public void onPlayerKickEvent(final PlayerKickEvent event) {
        Player player = event.getPlayer();
        if (manager.exit(player)) {
            plugin.getLogger().info("Account " + player.getName() + " logged out.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    @SuppressWarnings("unused")
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        Player player =  event.getPlayer();
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!manager.isAuth(player.getUniqueId())) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new AuthMessage(plugin, player));
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    @SuppressWarnings("unused")
    public void onPlayerMove(final PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!manager.isAuth(player.getUniqueId())) {
            Location from = event.getFrom();
            Location to = event.getTo();
            if (to.getX() != from.getX() || to.getY() != from.getY() || to.getZ() != from.getZ()) {
                player.teleport(from);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    @SuppressWarnings("unused")
    public void onEntityDamage(final EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            final Player player = (Player) entity;
            if (!manager.isAuth(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    @SuppressWarnings("unused")
    public void onInventoryInteract(final InventoryClickEvent event) {
        if (!manager.isAuth(event.getWhoClicked().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    @SuppressWarnings("unused")
    public void onItemDrop(final PlayerDropItemEvent event) {
		if (!manager.isAuth(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
	}

    @EventHandler(ignoreCancelled = true)
    @SuppressWarnings("unused")
    public void onInteract(final PlayerInteractEvent event) {
		if (!manager.isAuth(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
	}

    @EventHandler(ignoreCancelled = true)
    @SuppressWarnings("unused")
    public void onEntityInteract(final PlayerInteractEntityEvent event) {
		if (!manager.isAuth(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    @SuppressWarnings("unused")
    public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
        if (!manager.isAuth(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
