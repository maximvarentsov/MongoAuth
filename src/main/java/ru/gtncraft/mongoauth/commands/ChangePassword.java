package ru.gtncraft.mongoauth.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.Account;
import ru.gtncraft.mongoauth.AuthManager;
import ru.gtncraft.mongoauth.Messages;
import ru.gtncraft.mongoauth.MongoAuth;

import java.util.List;

public class ChangePassword implements CommandExecutor {

    private final MongoAuth plugin;
    private final AuthManager authManager;
	
	public ChangePassword(final MongoAuth instance) {
        this.plugin = instance;
        this.authManager = instance.getAuthManager();

        final PluginCommand command = this.plugin.getCommand("changepassword");
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

        final Account account = authManager.get(sender.getName());

        if (account == null) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.command_register_hint));
            return true;
        }

        if (!authManager.isAuth(account.getName())) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.command_login_hint));
            return true;
        }

        try {
            final String currentPassword = args[0];
            try {
                final String newPassword = args[1];
                if (account.checkPassword(currentPassword)) {
                    if (currentPassword.equals(newPassword)) {
                        sender.sendMessage(plugin.getConfig().getMessage(Messages.error_input_passwords_equals));
                        return true;
                    }
                    account.setPassword(newPassword);
                    authManager.save(account);
                    plugin.getLogger().info("Player " + account + " has changed password.");
                    sender.sendMessage(plugin.getConfig().getMessage(Messages.success_change_password));
                } else {
                    sender.sendMessage(plugin.getConfig().getMessage(Messages.error_input_password_missmach));
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                sender.sendMessage(plugin.getConfig().getMessage(Messages.error_input_password_new));
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.error_input_password));
        }
        return true;
    }
}