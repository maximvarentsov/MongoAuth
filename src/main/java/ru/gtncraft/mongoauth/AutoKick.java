package ru.gtncraft.mongoauth;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public class AutoKick {

    public AutoKick(final MongoAuth plugin) {
        final int minutes = plugin.getConfig().getInt("session.autokick", 2);
        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Date outdated = new Date();
                outdated.setMinutes(outdated.getMinutes() - minutes);
                for (Session session : plugin.getSessions()) {
                    UUID id = session.getAccount().getId();
                    if (plugin.getSessions().notAuthenticated(id)) {
                        if (session.getConnected().after(outdated)) {
                            Player player = Bukkit.getServer().getPlayer(id);
                            player.kickPlayer(Translations.get(Message.error_autokick));
                        }
                    }
                }
            }
        }, 0L, 600L);
    }
}
