package ru.gtncraft.mongoauth.commands;

import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.*;
import ru.gtncraft.mongoauth.database.Account;
import ru.gtncraft.mongoauth.database.Log;

public class Login extends Command {

    public Login(final MongoAuth plugin) {
        super(plugin);
        plugin.getCommand("login").setExecutor(this);
	}

    @Override
    public Message execute(Player player, String command, String[] args) {
        if (args.length < 1) {
            return Message.error_input_password;
        }

        Account account = getAccount(player);

        if (account == null) {
            return Message.command_register_hint;
        }

        if (!account.isAllowed()) {
            return Message.error_account_is_block;
        }

        if (isAuthorized(player)) {
            return Message.error_account_is_auth;
        }

        if (!account.getPassword().equals(encryptPassword(args[0]))) {
            return Message.error_input_password_missmach;
        }

        getManager().login(player);
        getLogger().info("Player " + player.getName() + " logged in.");

        getManager().log(player, Log.Status.DISCONNECT);

        return Message.success_account_login;
    }
}