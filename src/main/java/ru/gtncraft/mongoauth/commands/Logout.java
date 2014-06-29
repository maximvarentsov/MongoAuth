package ru.gtncraft.mongoauth.commands;

import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.Messages;
import ru.gtncraft.mongoauth.MongoAuth;

public class Logout extends Command {

	public Logout(final MongoAuth plugin) {
        super(plugin);
        PluginCommand pluginCommand = plugin.getCommand("logout");
        pluginCommand.setExecutor(this);
        pluginCommand.setPermissionMessage(getPlugin().getConfig().getMessage(Messages.error_command_permission));
    }

    @Override
    public Message execute(Player player, String command, String[] args) {

        if (getAccount(player) == null) {
            return new Message(Messages.command_register_hint);
        }

        if (!isAuthorized(player)) {
            return new Message(Messages.command_login_hint);
        }

        logout(player);
        getLogger().info("Player " + player.getName() + " logout.");

        return new Message(Messages.success_account_logout);
    }
}