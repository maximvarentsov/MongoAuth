package ru.gtncraft.mongoauth;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

class EmergencyListeners implements Listener {

    final String message;

    public EmergencyListeners(final MongoAuth plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        message = plugin.getConfig().getMessage(Messages.error_emergency);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    @SuppressWarnings("unused")
    public void onCommand(final PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(message);
        event.setCancelled(true);
    }

    @SuppressWarnings("unused")
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerMove(final PlayerMoveEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(message);
        event.setCancelled(true);
    }

    @SuppressWarnings("unused")
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamage(final EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    @SuppressWarnings("unused")
    public void onInventoryInteract(final InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    @SuppressWarnings("unused")
    public void onItemDrop(final PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    @SuppressWarnings("unused")
    public void onInteract(final PlayerInteractEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    @SuppressWarnings("unused")
    public void onEntityInteract(final PlayerInteractEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    @SuppressWarnings("unused")
    public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    @SuppressWarnings("unused")
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
        event.setCancelled(true);
    }
}
