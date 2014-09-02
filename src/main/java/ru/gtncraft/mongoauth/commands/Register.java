package ru.gtncraft.mongoauth.commands;

import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.*;

import static ru.gtncraft.mongoauth.util.Strings.encryptPassword;

public class Register extends Command {

	public Register(final MongoAuth plugin) {
        super(plugin);
        PluginCommand pluginCommand = plugin.getCommand("register");
        pluginCommand.setExecutor(this);
        pluginCommand.setPermissionMessage(getPlugin().getConfig().getMessage(Messages.error_command_permission));
	}

    @Override
    public Message execute(Player player, String command, String[] args) {

        if (isAuthorized(player)) {
            return new Message(Messages.error_account_is_auth);
        }

        if (getAccount(player) != null) {
            return new Message(Messages.command_login_hint);
        }

        Account account = new Account(player);

        if (args.length < 1) {
            return new Message(Messages.error_input_password);
        }

        if (getManager().registrationLimitMax(account)) {
            return new Message(Messages.error_account_register_limit);
        }

        account.setPassword(encryptPassword(args[0]));

        getManager().save(account);
        getManager().login(player);

        getLogger().info("New player " + player.getName() + " registered.");

        return new Message(Messages.success_account_create);
    }
}
