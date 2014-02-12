package ru.gtncraft.mongoauth.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.Account;
import ru.gtncraft.mongoauth.AuthManager;
import ru.gtncraft.mongoauth.Messages;
import ru.gtncraft.mongoauth.MongoAuth;

import java.util.List;

public class Register implements CommandExecutor {

    private final MongoAuth plugin;
    private final AuthManager authManager;
	
	public Register(final MongoAuth instance) {
        this.plugin = instance;
        this.authManager = instance.getAuthManager();

        final PluginCommand command = this.plugin.getCommand("register");
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
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.error_command_sender));
            return false;
        }

        final Account account = new Account((Player) sender);

        if (authManager.isAuth(account.getName())) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.error_account_is_auth));
            return true;
        }

        if (authManager.get(account.getName()) != null) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.error_account_exists));
            return true;
        }

        if (!authManager.checkRegisterLimit(account)) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.error_account_register_limit));
            return true;
        }

        try {
            final String password = args[0];
            account.setPassword(password);
            authManager.save(account);
            authManager.login(account.getName());
            sender.sendMessage(plugin.getConfig().getMessage(Messages.success_account_create));
            plugin.getLogger().info("New player " + account + " registered.");
        } catch (ArrayIndexOutOfBoundsException ex) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.error_input_password));
        }

        return true;
    }
}
