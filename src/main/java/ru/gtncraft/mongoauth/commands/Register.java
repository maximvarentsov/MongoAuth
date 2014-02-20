package ru.gtncraft.mongoauth.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.*;

import java.util.List;

public class Register implements CommandExecutor, TabCompleter {

    private final MongoAuth plugin;
    private final AuthManager authManager;
    private final Config config;
	
	public Register(final MongoAuth plugin) {
        this.plugin = plugin;
        this.authManager = plugin.getAuthManager();
        this.config = plugin.getConfig();
        final PluginCommand command = plugin.getCommand("register");
        command.setExecutor(this);
        command.setPermissionMessage(plugin.getConfig().getMessage(Messages.error_command_permission));
	}

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(config.getMessage(Messages.error_command_sender));
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                final Account account = new Account((Player) sender);

                if (authManager.isAuth(account.getName())) {
                    sender.sendMessage(config.getMessage(Messages.error_account_is_auth));
                    return;
                }

                if (authManager.get(account.getName()) != null) {
                    sender.sendMessage(config.getMessage(Messages.command_login_hint));
                    return;
                }

                if (args.length < 1) {
                    sender.sendMessage(config.getMessage(Messages.error_input_password));
                    return;
                }

                if (authManager.registrationLimitMax(account)) {
                    sender.sendMessage(config.getMessage(Messages.error_account_register_limit));
                    return;
                }
                account.setPassword(args[0]);
                authManager.save(account);
                authManager.login((Player) sender);
                sender.sendMessage(config.getMessage(Messages.success_account_create));
                plugin.getLogger().info("New player " + sender.getName() + " registered.");
            }
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return ImmutableList.of();
    }
}
