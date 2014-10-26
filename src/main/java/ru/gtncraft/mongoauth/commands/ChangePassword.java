package ru.gtncraft.mongoauth.commands;

import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.Session;
import ru.gtncraft.mongoauth.Message;
import ru.gtncraft.mongoauth.MongoAuth;

public class ChangePassword extends Command {

	public ChangePassword(final MongoAuth plugin) {
        super(plugin);
        plugin.getCommand("changepassword").setExecutor(this);
	}

    @Override
    public Message execute(Player player, String[] args) {
        if (args.length < 1) {
            return Message.error_input_password;
        }

        if (args.length < 2) {
            return Message.error_input_password_new;
        }

        Session session = getSession(player);

        if (!session.isRegister()) {
            return Message.command_register_hint;
        }

        if (!isAuthorized(player)) {
            return Message.command_login_hint;
        }

        String currentPassword = args[0];
        String newPassword = args[1];

        if (currentPassword.equals(newPassword)) {
            return Message.error_input_passwords_equals;
        }

        if (!session.checkPassword(currentPassword)) {
            return Message.error_input_password_missmach;
        }

        session.getAccount().setPassword(newPassword);
        getDatabase().saveAccount(session.getAccount());

        getLogger().info("Player " + player.getName() + " has changed password.");

        return Message.success_change_password;
    }
}
