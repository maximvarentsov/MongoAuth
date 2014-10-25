package ru.gtncraft.mongoauth.commands;

import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.Message;
import ru.gtncraft.mongoauth.MongoAuth;

public class Logout extends Command {

	public Logout(final MongoAuth plugin) {
        super(plugin);
        PluginCommand pluginCommand = plugin.getCommand("logout");
        pluginCommand.setExecutor(this);
    }

    @Override
    public Message execute(Player player, String command, String[] args) {
        if (getAccount(player) == null) {
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