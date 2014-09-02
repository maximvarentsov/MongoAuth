package ru.gtncraft.mongoauth.commands;

import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.*;

import static ru.gtncraft.mongoauth.util.Strings.encryptPassword;

public class Login extends Command {

    public Login(final MongoAuth plugin) {
        super(plugin);
        PluginCommand pluginCommand = plugin.getCommand("login");
        pluginCommand.setExecutor(this);
        pluginCommand.setPermissionMessage(getPlugin().getConfig().getMessage(Messages.error_command_permission));
	}

    @Override
    public Message execute(Player player, String command, String[] args) {

        if (args.length < 1) {
            return new Message(Messages.error_input_password);
        }

        Account account = getAccount(player);

        if (account == null) {
            return new Message(Messages.command_register_hint);
        }

        if (account.isBlocked()) {
            return new Message(Messages.error_account_is_block);
        }

        if (isAuthorized(player)) {
            return new Message(Messages.error_account_is_auth);
        }

        if (!account.getPassword().equals(encryptPassword(args[0]))) {
            return new Message(Messages.error_input_password_missmach);
        }

        getManager().login(player);
        getLogger().info("Player " + player.getName() + " logged in.");

        return new Message(Messages.success_account_login);
    }
}