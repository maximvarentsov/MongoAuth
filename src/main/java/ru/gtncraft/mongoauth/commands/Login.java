package ru.gtncraft.mongoauth.commands;

import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.*;
import ru.gtncraft.mongoauth.database.Account;

import static ru.gtncraft.mongoauth.util.Password.encrypt;

public class Login extends Command {
    private final int maxAttempts;

    public Login(final MongoAuth plugin) {
        super(plugin);
        maxAttempts = plugin.getConfig().getInt("session.attempts", 3);
        plugin.getCommand("login").setExecutor(this);
	}

    @Override
    public Message execute(Player player, String[] args) {
        if (args.length < 1) {
            return Message.error_input_password;
        }

        if (isAuthorized(player)) {
            return Message.error_account_is_auth;
        }

        Account account = getDatabase().getAccount(player);

        if (account == null) {
            return Message.command_register_hint;
        }

        if (player.getUniqueId().equals(account.getId())) {
            return Message.error_invalid_uuid;
        }

        if (!account.isAllowed()) {
            return Message.error_account_is_block;
        }

        Session session = getSession(player);

        if (!checkAttempts(session)) {
            return Message.error_password_brutforce;
        }

        String password = encrypt(args[0]);

        if (account.getPassword().equals(password)) {
            return Message.error_input_password_missmach;
        }

        login(player);

        getLogger().info("Player " + player.getName() + " logged in.");

        return Message.success_account_login;
    }

    private boolean checkAttempts(Session session) {
        return session.getAttempts() <= maxAttempts;
    }
}