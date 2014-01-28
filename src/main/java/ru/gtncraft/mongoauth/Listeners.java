package ru.gtncraft.mongoauth;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.util.regex.Pattern;

public class Listeners implements Listener {

    private final Storage storage;
	private final MongoAuth plugin;
	private final SessionManager sm;
    private final boolean whiteList;
    private final Pattern pattern;

	public Listeners(final MongoAuth instance) {
        this.plugin = instance;
        this.plugin.getServer().getPluginManager().registerEvents(this, instance);
        this.sm = instance.getSessionManager();
        this.storage = instance.getStorage();
        this.whiteList = instance.getConfig().getBoolean("general.whitelist", false);
        this.pattern = Pattern.compile(instance.getConfig().getString("general.playernamePattern", "*"));
	}

    @EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerPreLogin(final AsyncPlayerPreLoginEvent event) {
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
	public void onCommand(final PlayerCommandPreprocessEvent event) {
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
    public void onPlayerMove(final PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!sm.contains(player.getName())) {
            player.teleport(event.getFrom());
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Account account = storage.get(player.getName());

        if (account == null) {
            player.sendMessage(Message.REGISTER_COMMAND_HINT);
        } else {
            player.sendMessage(Message.LOGIN_COMMAND_HINT);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuitEvent(final PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (sm.contains(player.getName())) {
            sm.remove(player.getName());
            plugin.getLogger().info("Account " + player.getName() + " logged out.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!sm.contains(player.getName())) {
            player.sendMessage(Message.REGISTER_OR_LOGIN);
            event.setCancelled(true);
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent event) {
        Entity entity = event.getEntity();
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
