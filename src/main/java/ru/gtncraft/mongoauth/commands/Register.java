package ru.gtncraft.mongoauth.commands;

import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.*;

import java.util.UUID;

public class Register extends Command {
	public Register(final MongoAuth plugin) {
        super(plugin);
        plugin.getCommand("register").setExecutor(this);
	}

    @Override
    public Message execute(Player player, String command, String[] args) {
        if (isAuthorized(player)) {
            return Message.error_account_is_auth;
        }

        if (getAccount(player) != null) {
            return Message.command_login_hint;
        }

        if (args.length < 1) {
            return Message.error_input_password;
        }

        UUID uuid = player.getUniqueId();
        long ip = dot2LongIP(player.getAddress().getAddress().getHostAddress());
        String password = encryptPassword(args[0]);

        Account account = new Account(uuid, ip, password);

        if (getManager().registrationLimitMax(account)) {
            return Message.error_account_register_limit;
        }

        getManager().save(account);
        getManager().login(player);

        getLogger().info("New player " + player.getName() + " registered.");

        return Message.success_account_create;
    }
}
