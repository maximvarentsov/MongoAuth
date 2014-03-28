package ru.gtncraft.mongoauth.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.*;

import java.util.List;

public class Logout implements CommandExecutor, TabCompleter {

	private final AuthManager authManager;
    private final Config config;
    private final MongoAuth plugin;
	
	public Logout(final MongoAuth plugin) {
		this.authManager = plugin.getAuthManager();
        this.config = plugin.getConfig();
        this.plugin = plugin;
        final PluginCommand command = plugin.getCommand("logout");
        command.setExecutor(this);
        command.setPermissionMessage(config.getMessage(Messages.error_command_permission));
	}

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(config.getMessage(Messages.error_command_sender));
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final Account account = plugin.getAuthManager().get(sender.getName());
            if (account == null) {
                sender.sendMessage(plugin.getConfig().getMessage(Messages.command_register_hint));
            } else {
                sender.sendMessage(plugin.getConfig().getMessage(Messages.command_login_hint));
            }
            if (authManager.logout(sender.getName())) {
                plugin.getLogger().info("Player " + sender.getName() + " logget out.");
                sender.sendMessage(config.getMessage(Messages.success_account_logout));
            }
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return ImmutableList.of();
    }
}