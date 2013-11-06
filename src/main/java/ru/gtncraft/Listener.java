package ru.gtncraft;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import java.util.regex.Pattern;

public class Listener implements org.bukkit.event.Listener {

    private Storage storage;
	private MongoAuth plugin;
	private Sessions sm;

    final private boolean silentMode;
    final private boolean whiteList;
    final private Pattern pattern;

	public Listener(MongoAuth instance, Storage storage, Sessions sm) {
        instance.getServer().getPluginManager().registerEvents(this, instance);
        this.plugin = instance;
        this.sm = sm;
        this.storage = storage;

        ConfigurationSection config = instance.getConfig().getConfigurationSection("general");

        this.silentMode = config.getBoolean("silentMode");
        this.whiteList = config.getBoolean("whitelist");
        this.pattern = Pattern.compile(config.getString("playernamePattern"));
	}

    @EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		String playername = event.getName();

        if (!pattern.matcher(playername).matches()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Message.LOGIN_NOT_VALID);
            return;
        }

		for (Player online : plugin.getServer().getOnlinePlayers()) {
            if (online.getName().equalsIgnoreCase(playername)) {
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage(String.format(Message.PLAYER_ALREADY_ONLINE, playername));
                return;
            }
		}

        if (whiteList) {
            Account account = storage.get(playername);
            if (!account.isAllowed()) {
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage(Message.WHITELIST);
            }
        }
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player =  event.getPlayer();
		String command = event.getMessage().substring(1);
		String rootCommand = command.split(" ")[0];
		if (plugin.getCommand(rootCommand) != null && !plugin.getCommand(rootCommand).equals(plugin.getCommand("mongoauth"))) {
            return;
		}
		if (!sm.contains(player.getName())) {
			 player.sendMessage(Message.REGISTER_OR_LOGIN);
			 event.setCancelled(true);
		}
	}

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!sm.contains(player.getName())) {
            Location from = event.getFrom();
            Location to = new Location(from.getWorld(), from.getX(), from.getY(), from.getZ());
            to.setPitch(from.getPitch());
            to.setYaw(from.getYaw());
            player.teleport(to);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (silentMode) {
            event.setJoinMessage(null);
        }
        Player player = event.getPlayer();

        Account account = storage.get(player.getName());

        if (account == null) {
            player.sendMessage(Message.REGISTER_COMMAND_HINT);
        } else {
            player.sendMessage(Message.LOGIN_COMMAND_HINT);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (silentMode) {
            event.setQuitMessage(null);
        }
        if (sm.contains(player.getName())) {
            sm.remove(player.getName());
            plugin.getLogger().info("Account " + player.getName() + " logged out.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!sm.contains(player.getName())) {
            player.sendMessage(Message.REGISTER_OR_LOGIN);
            event.setCancelled(true);
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (!sm.contains(player.getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryInteract(InventoryClickEvent event) {
        if (!sm.contains(event.getWhoClicked().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
	public void onItemDrop(PlayerDropItemEvent event) {
		if (!sm.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
	}

    @EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		if (!sm.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
	}

    @EventHandler(ignoreCancelled = true)
	public void onEntityInteract(PlayerInteractEntityEvent event) {
		if (!sm.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPickUp(PlayerPickupItemEvent event) {
        if (!sm.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }
}
