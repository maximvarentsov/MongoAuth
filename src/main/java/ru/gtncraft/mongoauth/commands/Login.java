package ru.gtncraft.mongoauth.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.*;

import java.util.List;

class Login implements CommandExecutor, TabExecutor {

    final MongoAuth plugin;
    final Config config;
	final AuthManager authManager;

    public Login(final MongoAuth plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
		this.authManager = plugin.getAuthManager();

        PluginCommand command = plugin.getCommand("login");
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
            final Account account = authManager.get(sender.getName());

            if (account == null) {
                sender.sendMessage(config.getMessage(Messages.command_register_hint));
                return;
            }

            if (!account.isAllowed()) {
                sender.sendMessage(config.getMessage(Messages.error_account_is_block));
                return;
            }

            if (authManager.isAuth(sender.getName())) {
                sender.sendMessage(config.getMessage(Messages.error_account_is_auth));
                return;
            }

            if (args.length < 1) {
                sender.sendMessage(config.getMessage(Messages.error_input_password));
                return;
            }

            if (!account.checkPassword(args[0])) {
                sender.sendMessage(config.getMessage(Messages.error_input_password_missmach));
                return;
            }

            authManager.login((Player) sender);
            sender.sendMessage(config.getMessage(Messages.success_account_login));
            plugin.getLogger().info("Player " + sender.getName() + " logged in.");
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return ImmutableList.of();
    }
}