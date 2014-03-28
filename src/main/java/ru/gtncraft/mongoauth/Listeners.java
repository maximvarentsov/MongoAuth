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
import java.util.regex.Pattern;

public class Listeners implements Listener {

	private final MongoAuth plugin;
	private final AuthManager manager;
    private final Pattern pattern;
    private final Config config;

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
        final String playername = event.getName();
        if (!pattern.matcher(playername).matches()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(config.getMessage(Messages.error_input_invalid_login));
            return;
        }
		for (Player online : plugin.getServer().getOnlinePlayers()) {
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
        final Player player = event.getPlayer();
        manager.join(player);
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new AuthMessage(plugin, player));
    }

    @EventHandler(ignoreCancelled = true)
    @SuppressWarnings("unused")
    public void onPlayerKickEvent(final PlayerKickEvent event) {
        final Player player = event.getPlayer();
        if (manager.exit(player)) {
            plugin.getLogger().info("Account " + player.getName() + " logged out.");
        }
    }

    @EventHandler()
    @SuppressWarnings("unused")
    public void onPlayerQuitEvent(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (manager.exit(player)) {
            plugin.getLogger().info("Account " + player.getName() + " logged out.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    @SuppressWarnings("unused")
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        final Player player =  event.getPlayer();
        final String command = event.getMessage().substring(1);
        final String rootCommand = command.split(" ")[0];
        if (plugin.getCommand(rootCommand) != null && !plugin.getCommand(rootCommand).equals(plugin.getCommand("mongoauth"))) {
            return;
        }
        if (!manager.isAuth(player.getName())) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new AuthMessage(plugin, player));
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        if (!manager.isAuth(player.getName())) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new AuthMessage(plugin, player));
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    @SuppressWarnings("unused")
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (!manager.isAuth(player.getName())) {
            final Location from = event.getFrom();
            final Location to = event.getTo();
            if (to.getX() != from.getX() || to.getY() != from.getY() || to.getZ() != from.getZ()) {
                player.teleport(from);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    @SuppressWarnings("unused")
    public void onEntityDamage(final EntityDamageEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof Player) {
            final Player player = (Player) entity;
            if (!manager.isAuth(player.getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    @SuppressWarnings("unused")
    public void onInventoryInteract(final InventoryClickEvent event) {
        if (!manager.isAuth(event.getWhoClicked().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    @SuppressWarnings("unused")
    public void onItemDrop(final PlayerDropItemEvent event) {
		if (!manager.isAuth(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
	}

    @EventHandler(ignoreCancelled = true)
    @SuppressWarnings("unused")
    public void onInteract(final PlayerInteractEvent event) {
		if (!manager.isAuth(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
	}

    @EventHandler(ignoreCancelled = true)
    @SuppressWarnings("unused")
    public void onEntityInteract(final PlayerInteractEntityEvent event) {
		if (!manager.isAuth(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    @SuppressWarnings("unused")
    public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
        if (!manager.isAuth(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }
}
