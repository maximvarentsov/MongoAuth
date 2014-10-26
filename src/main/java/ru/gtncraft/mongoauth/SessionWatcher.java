package ru.gtncraft.mongoauth;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;
import java.util.logging.Logger;

public class SessionWatcher implements PluginMessageListener {
    private final static String channel = "mongoauth";
    private final Sessions sessions;
    private final Logger logger;

    public SessionWatcher(MongoAuth plugin) {
        sessions = plugin.getSessions();
        logger = plugin.getLogger();
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(plugin, channel, this);
    }

    @Override
    public void onPluginMessageReceived(String s, Player p, byte[] bytes) {
        UUID id = UUID.fromString(new String(bytes));
        if (sessions.quit(id)) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(id);
            logger.info("Account " + player.getName() + " logged out.");
        }
    }
}
