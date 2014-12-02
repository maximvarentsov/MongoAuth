package ru.gtncraft.mongoauth.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.*;
import ru.gtncraft.mongoauth.database.Account;

import static ru.gtncraft.mongoauth.util.Password.encrypt;

public class Unregister extends Command {

	public Unregister(final MongoAuth plugin) {
        super(plugin);
        plugin.getCommand("unregister").setExecutor(this);
	}

    @Override
    public Message execute(Player player, String[] args) {
        if (args.length < 1) {
            return Message.error_input_password;
        }

        Account account = getDatabase().getAccount(player);

        if (account == null) {
            return Message.command_register_hint;
        }

        if (!isAuthorized(player)) {
            return Message.command_login_hint;
        }

        String password = encrypt(args[0]);

        if (!account.getPassword().equals(password)) {
            return Message.error_input_password_missmach;
        }

        getDatabase().deleteAccount(account);
        logout(player);

        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFoodLevel(20);
        player.setExp(0);

        getLogger().info("Account " + player.getName() + " unregistered.");
        return Message.success_account_delete;
    }
}
