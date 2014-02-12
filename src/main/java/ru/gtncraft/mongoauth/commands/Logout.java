package ru.gtncraft.mongoauth.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.Account;
import ru.gtncraft.mongoauth.AuthManager;
import ru.gtncraft.mongoauth.Messages;
import ru.gtncraft.mongoauth.MongoAuth;

import java.util.List;

public class Logout implements CommandExecutor {

    private final MongoAuth plugin;
	private final AuthManager authManager;
	
	public Logout(final MongoAuth instance) {
        this.plugin = instance;
		this.authManager = instance.getAuthManager();

        final PluginCommand command = this.plugin.getCommand("logout");
        command.setExecutor(this);
        command.setPermissionMessage(plugin.getConfig().getMessage(Messages.error_command_permission));
        command.setTabCompleter(new TabCompleter() {
            @Override
            public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
                return ImmutableList.of();
            }
        });
	}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.error_command_sender));
            return false;
        }

        final Account account = new Account((Player) sender);

        if (authManager.logout(account.getName())) {
            plugin.getLogger().info("Player " + account + " logget out.");
            sender.sendMessage(plugin.getConfig().getMessage(Messages.success_account_logout));
        }

        return true;
    }
}