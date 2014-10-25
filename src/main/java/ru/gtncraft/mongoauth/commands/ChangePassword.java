package ru.gtncraft.mongoauth.commands;

import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.Account;
import ru.gtncraft.mongoauth.Message;
import ru.gtncraft.mongoauth.MongoAuth;

public class ChangePassword extends Command {

	public ChangePassword(final MongoAuth plugin) {
        super(plugin);
        PluginCommand pluginCommand = plugin.getCommand("changepassword");
        pluginCommand.setExecutor(this);
	}

    @Override
    public Message execute(Player player, String command, String[] args) {
        if (args.length < 1) {
            return Message.error_input_password;
        }

        if (args.length < 2) {
            return Message.error_input_password_new;
        }

        Account account = getAccount(player);

        if (account == null) {
            return Message.command_register_hint;
        }

        if (isAuthorized(player)) {
            return Message.command_login_hint;
        }

        String currentPassword = encryptPassword(args[0]);
        String newPassword = args[1];

        if (currentPassword.equals(args[0])) {
            return Message.error_input_passwords_equals;
        }

        if (!account.getPassword().equals(currentPassword)) {
            return Message.error_input_password_missmach;
        }

        account.setPassword(newPassword);
        getManager().save(account);

        getLogger().info("Player " + player.getName() + " has changed password.");

        return Message.success_change_password;
    }
}
