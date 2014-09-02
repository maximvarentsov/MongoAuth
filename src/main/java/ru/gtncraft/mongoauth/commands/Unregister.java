package ru.gtncraft.mongoauth.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.*;

import static ru.gtncraft.mongoauth.util.Strings.encryptPassword;

public class Unregister extends Command {

	public Unregister(final MongoAuth plugin) {
        super(plugin);
        PluginCommand pluginCommand = plugin.getCommand("unregister");
        pluginCommand.setExecutor(this);
        pluginCommand.setPermissionMessage(getPlugin().getConfig().getMessage(Messages.error_command_permission));
	}

    @Override
    public Message execute(Player player, String command, String[] args) {

        Account account = getAccount(player);

        if (account == null) {
            return new Message(Messages.command_register_hint);
        }

        if (!isAuthorized(player)) {
            return new Message(Messages.command_login_hint);
        }

        if (args.length < 1) {
            return new Message(Messages.error_input_password);
        }

        if (!account.getPassword().equals(encryptPassword(args[0]))) {
            return new Message(Messages.error_input_password_missmach);
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
        return new Message(Messages.success_account_delete);
    }
}
