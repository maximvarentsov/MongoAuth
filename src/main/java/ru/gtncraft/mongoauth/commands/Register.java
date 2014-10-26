package ru.gtncraft.mongoauth.commands;

import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.*;
import ru.gtncraft.mongoauth.database.Account;

import java.util.UUID;

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

        Session session = getSession(player);

        if (isAuthorized(player)) {
            return Message.error_account_is_auth;
        }

        if (session.isRegister()) {
            return Message.command_login_hint;
        }

        UUID uuid = player.getUniqueId();
        long ip = dot2LongIP(player.getAddress().getAddress().getHostAddress());
        String password = encrypt(args[0]);

        Account account = new Account(uuid, ip, password);

        if (checkRegistrationLimit(ip)) {
            return Message.error_account_register_limit;
        }

        session.setAccount(account);
        getDatabase().saveAccount(account);
        login(player);


        getLogger().info("New player " + player.getName() + " registered.");

        return Message.success_account_create;
    }
}
