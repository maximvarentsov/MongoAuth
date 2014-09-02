package ru.gtncraft.mongoauth.tasks;

import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.Account;
import ru.gtncraft.mongoauth.Message;
import ru.gtncraft.mongoauth.Messages;
import ru.gtncraft.mongoauth.MongoAuth;

public class AuthMessage implements Runnable {
    private final MongoAuth plugin;
    private final Player player;

    public AuthMessage(final MongoAuth plugin, final Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run() {
        if (player != null) {
            Account account = plugin.getAuthManager().get(player.getUniqueId());
            if (account == null) {
                player.sendMessage(Messages.get(Message.command_register_hint));
            } else {
                player.sendMessage(Messages.get(Message.command_login_hint));
            }
        }
    }
}
