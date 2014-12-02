package ru.gtncraft.mongoauth.commands;

import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.*;
import ru.gtncraft.mongoauth.database.Account;

import static ru.gtncraft.mongoauth.util.Password.encrypt;

public class Register extends Command {
	public Register(final MongoAuth plugin) {
        super(plugin);
        plugin.getCommand("register").setExecutor(this);
	}

    @Override
    public Message execute(Player player, String[] args) {
        if (args.length < 1) {
            return Message.error_input_password;
        }

        if (isAuthorized(player)) {
            return Message.error_account_is_auth;
        }

        if (getDatabase().getAccount(player) != null) {
            return Message.command_login_hint;
        }

        long ip = dot2LongIP(player.getAddress().getAddress().getHostAddress());
        if (checkRegistrationLimit(ip)) {
            return Message.error_account_register_limit;
        }

        String login = player.getName().toLowerCase();
        String password = encrypt(args[0]);

        Account account = new Account(login, ip, password);

        getDatabase().createAccount(account);
        login(player);

        getLogger().info("New player " + login + " registered.");

        return Message.success_account_create;
    }
}
