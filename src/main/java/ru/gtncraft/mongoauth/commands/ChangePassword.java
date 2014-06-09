package ru.gtncraft.mongoauth.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.*;
import java.util.List;

class ChangePassword implements CommandExecutor, TabCompleter {

    final Config config;
    final AuthManager authManager;
    final MongoAuth plugin;
	
	public ChangePassword(final MongoAuth plugin) {
        this.config = plugin.getConfig();
        this.authManager = plugin.getAuthManager();
        this.plugin = plugin;

        PluginCommand command = plugin.getCommand("changepassword");
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

            if (!authManager.isAuth(account.getName())) {
                sender.sendMessage(config.getMessage(Messages.command_login_hint));
                return;
            }

            if (args.length < 1) {
                sender.sendMessage(config.getMessage(Messages.error_input_password));
                return;
            }

            if (args.length < 2) {
                sender.sendMessage(config.getMessage(Messages.error_input_password_new));
                return;
            }

            final String currentPassword = args[0];
            final String newPassword = args[1];
            if (!account.checkPassword(currentPassword)) {
                sender.sendMessage(config.getMessage(Messages.error_input_password_missmach));
                return;
            }

            if (currentPassword.equals(newPassword)) {
                sender.sendMessage(config.getMessage(Messages.error_input_passwords_equals));
                return;
            }

            account.setPassword(newPassword);
            authManager.save(account);
            plugin.getLogger().info("Player " + sender.getName() + " has changed password.");
            sender.sendMessage(config.getMessage(Messages.success_change_password));
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] strings) {
        return ImmutableList.of();
    }
}
