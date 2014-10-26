package ru.gtncraft.mongoauth.commands;

import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.Message;
import ru.gtncraft.mongoauth.MongoAuth;
import ru.gtncraft.mongoauth.Session;

public class Logout extends Command {

	public Logout(final MongoAuth plugin) {
        super(plugin);
        plugin.getCommand("logout").setExecutor(this);
    }

    @Override
    public Message execute(Player player, String[] args) {
        Session session = getSession(player);

        if (!session.isRegister()) {
            return Message.command_register_hint;
        }

        if (!isAuthorized(player)) {
            return Message.command_login_hint;
        }

        logout(player);
        getLogger().info("Player " + player.getName() + " logout.");

        return Message.success_account_logout;
    }
}
