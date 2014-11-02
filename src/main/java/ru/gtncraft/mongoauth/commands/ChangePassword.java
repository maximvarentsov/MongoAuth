package ru.gtncraft.mongoauth.commands;

import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.Message;
import ru.gtncraft.mongoauth.MongoAuth;
import ru.gtncraft.mongoauth.database.Account;

import static ru.gtncraft.mongoauth.util.Password.encrypt;

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

        Account account = getDatabase().getAccount(player);

        if (account == null) {
            return Message.command_register_hint;
        }

        if (!isAuthorized(player)) {
            return Message.command_login_hint;
        }

        String currentPassword = encrypt(args[0]);
        String newPassword = encrypt(args[1]);

        if (currentPassword.equals(newPassword)) {
            return Message.error_input_passwords_equals;
        }

        if (!account.getPassword().equals(currentPassword)) {
            return Message.error_input_password_missmach;
        }

        account.setPassword(newPassword);
        getDatabase().saveAccount(account);

        getLogger().info("Player " + player.getName() + " has changed password.");

        return Message.success_change_password;
    }
}
