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
import ru.gtncraft.mongoauth.database.Database;
import ru.gtncraft.mongoauth.tasks.AuthMessage;

import java.util.regex.Pattern;

public class Listeners implements Listener {

    private final Database db;
	private final MongoAuth plugin;
	private final SessionManager sm;
    private final Pattern pattern;
    private final Config config;

	public Listeners(final MongoAuth instance) {
        this.plugin = instance;
        this.plugin.getServer().getPluginManager().registerEvents(this, instance);
        this.sm = instance.getSessionManager();
        this.db = instance.getDB();
        this.config = plugin.getConfig();
        this.pattern = Pattern.compile(this.config.getString("general.playernamePattern"));
	}

    @EventHandler (priority = EventPriority.LOWEST)
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

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onCommand(final PlayerCommandPreprocessEvent event) {
		final Player player =  event.getPlayer();
		final String command = event.getMessage().substring(1);
		final String rootCommand = command.split(" ")[0];

        if (plugin.getCommand(rootCommand) != null && !plugin.getCommand(rootCommand).equals(plugin.getCommand("mongoauth"))) {
            return;
		}

		if (!sm.contains(player.getName())) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new AuthMessage(plugin, player));
			event.setCancelled(true);
		}
	}

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (!sm.contains(player.getName())) {
            final Location from = event.getFrom();
            from.setPitch(player.getLocation().getPitch());
            from.setYaw(player.getLocation().getYaw());
            player.teleport(from);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new AuthMessage(plugin, player));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuitEvent(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (sm.contains(player.getName())) {
            sm.remove(player.getName());
            plugin.getLogger().info("Account " + player.getName() + " logged out.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        if (!sm.contains(player.getName())) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new AuthMessage(plugin, player));
            event.setCancelled(true);
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (!sm.contains(player.getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryInteract(final InventoryClickEvent event) {
        if (!sm.contains(event.getWhoClicked().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
	public void onItemDrop(final PlayerDropItemEvent event) {
		if (!sm.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
	}

    @EventHandler(ignoreCancelled = true)
	public void onInteract(final PlayerInteractEvent event) {
		if (!sm.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
	}

    @EventHandler(ignoreCancelled = true)
	public void onEntityInteract(final PlayerInteractEntityEvent event) {
		if (!sm.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
        if (!sm.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }
}
