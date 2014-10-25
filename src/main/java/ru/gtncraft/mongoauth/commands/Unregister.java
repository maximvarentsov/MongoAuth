package ru.gtncraft.mongoauth.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.*;

public class Unregister extends Command {

	public Unregister(final MongoAuth plugin) {
        super(plugin);
        PluginCommand pluginCommand = plugin.getCommand("unregister");
        pluginCommand.setExecutor(this);
	}

    @Override
    public Message execute(Player player, String command, String[] args) {
        Account account = getAccount(player);

        if (account == null) {
            return Message.command_register_hint;
        }

        if (!isAuthorized(player)) {
            return Message.command_login_hint;
        }

        if (args.length < 1) {
            return Message.error_input_password;
        }

        if (!account.getPassword().equals(encryptPassword(args[0]))) {
            return Message.error_input_password_missmach;
        }

        getManager().unregister(account);
        logout(player);

        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFoodLevel(20);
        player.setExp(0);

        getLogger().info("Account " + account + " unregistered.");
        return Message.success_account_delete;
    }
}
