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
	}

    @Override
    public String execute(Player player, String command, String[] args) {
        if (args.length < 1) {
            return Messages.get(Message.error_input_password);
        }

        Account account = getAccount(player);

        if (account == null) {
            return Messages.get(Message.command_register_hint);
        }

        if (account.isBlocked()) {
            return Messages.get(Message.error_account_is_block);
        }

        if (isAuthorized(player)) {
            return Messages.get(Message.error_account_is_auth);
        }

        if (!account.getPassword().equals(encryptPassword(args[0]))) {
            return Messages.get(Message.error_input_password_missmach);
        }

        getManager().login(player);
        getLogger().info("Player " + player.getName() + " logged in.");

        return Messages.get(Message.success_account_login);
    }
}