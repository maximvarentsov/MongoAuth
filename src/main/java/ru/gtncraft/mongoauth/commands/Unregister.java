package ru.gtncraft.mongoauth.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.*;

import java.util.List;
import java.util.logging.Logger;

public class Unregister implements CommandExecutor, TabCompleter {

	private final AuthManager authManager;
    private final Config config;
    private final Logger logger;
    private final MongoAuth plugin;

	public Unregister(final MongoAuth plugin) {
        this.config = plugin.getConfig();
		this.authManager = plugin.getAuthManager();
        this.logger = plugin.getLogger();
        this.plugin = plugin;
        final PluginCommand command = plugin.getCommand("unregister");
        command.setExecutor(this);
        command.setPermissionMessage(plugin.getConfig().getMessage(Messages.error_command_permission));
	}

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(config.getMessage(Messages.error_command_sender));
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final Account account = authManager.get(sender.getName());

            if (account == null) {
                sender.sendMessage(config.getMessage(Messages.command_register_hint));
                return ;
            }

            if (!authManager.isAuth(account.getName())) {
                sender.sendMessage(config.getMessage(Messages.command_login_hint));
                return ;
            }

            if (args.length < 1) {
                sender.sendMessage(config.getMessage(Messages.error_input_password));
                return;
            }

            if (!account.checkPassword(args[0])) {
                sender.sendMessage(config.getMessage(Messages.error_input_password_missmach));
                return;
            }

            authManager.unregister(account);
            authManager.logout(sender.getName());
            logger.info("Account " + account + " unregistered.");
            sender.sendMessage(config.getMessage(Messages.success_account_delete));

            // Clear player profile.
            // TODO: remove perms, worldprotect regions and other stuff.
            Player player = (Player) sender;
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(false);
            player.setFoodLevel(20);
            player.setExp(0);
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] strings) {
        return ImmutableList.of();
    }
}
