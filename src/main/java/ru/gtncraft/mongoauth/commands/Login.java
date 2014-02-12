package ru.gtncraft.mongoauth.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.Account;
import ru.gtncraft.mongoauth.AuthManager;
import ru.gtncraft.mongoauth.Messages;
import ru.gtncraft.mongoauth.MongoAuth;

import java.util.List;

public class Login implements CommandExecutor {

    private final MongoAuth plugin;
	private final AuthManager authManager;

    public Login(final MongoAuth instance) {
        this.plugin = instance;
		this.authManager = instance.getAuthManager();

        final PluginCommand command = this.plugin.getCommand("login");
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
            sender.sendMessage(plugin.getConfig().getMessage(Messages.error_account_not_registred));
            sender.sendMessage(plugin.getConfig().getMessage(Messages.command_register_hint));
            return true;
        }

        if (!account.isAllowed()) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.error_account_is_block));
            return true;
        }

        if (authManager.isAuth(sender.getName())) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.error_account_is_auth));
            return true;
        }

        try {
            final String password = args[0];
            if (account.checkPassword(password)) {
                authManager.login(account.getName());
                authManager.restoreLocation((Player) sender);
                sender.sendMessage(plugin.getConfig().getMessage(Messages.success_account_login));
                plugin.getLogger().info("Player " + account + " logged in.");
            } else {
                sender.sendMessage(plugin.getConfig().getMessage(Messages.error_input_password_missmach));
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.error_input_password));
        }
        return true;
    }
}