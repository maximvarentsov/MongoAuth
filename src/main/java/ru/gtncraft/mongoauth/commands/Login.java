package ru.gtncraft.mongoauth.commands;

import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.*;

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

        Session session = getSession(player);

        if (!session.isRegister()) {
            return Message.command_register_hint;
        }

        if (!session.getAccount().isAllowed()) {
            return Message.error_account_is_block;
        }

        if (isAuthorized(player)) {
            return Message.error_account_is_auth;
        }

        if (!checkAttempts(session)) {
            return Message.error_password_brutforce;
        }

        String password = args[0];
        if (!session.checkPassword(password)) {
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