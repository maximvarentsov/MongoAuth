package ru.gtncraft.mongoauth;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AutoKick {

    public AutoKick(final MongoAuth plugin) {
        final int autokick = plugin.getConfig().getInt("session.autokick", 120);
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                for (Session session : plugin.getSessions()) {
                    UUID id = session.getId();
                    if (plugin.getSessions().isGuest(id)) {
                        long diff = System.currentTimeMillis() - session.getConnected().getTime();
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
                        System.out.println("sec:" + seconds);
                        if (seconds > autokick) {
                            OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(id);
                            if (player.isOnline()) {
                                ((Player) player).kickPlayer(Translations.get(Message.error_autokick));
                            }
                        }
                    }
                }
            }
        }, 0L, 20L);
    }
}
