package ru.gtncraft.mongoauth.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.*;

public class Unregister extends Command {

	public Unregister(final MongoAuth plugin) {
        super(plugin);
        plugin.getCommand("unregister").setExecutor(this);
	}

    @Override
    public Message execute(Player player, String[] args) {
        Session session = getSession(player);

        if (session == null) {
            return Message.command_register_hint;
        }

        if (session.isRegister()) {
            return Message.command_login_hint;
        }

        if (args.length < 1) {
            return Message.error_input_password;
        }

        if (!session.checkPassword(args[0])) {
            return Message.error_input_password_missmach;
        }

        getDatabase().deleteAccount(player.getUniqueId());
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
