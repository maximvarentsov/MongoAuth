package ru.gtncraft.mongoauth.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.Account;
import ru.gtncraft.mongoauth.Messages;
import ru.gtncraft.mongoauth.MongoAuth;
import ru.gtncraft.mongoauth.SessionManager;
import ru.gtncraft.mongoauth.database.Database;

import java.util.List;

public class Changepassword implements CommandExecutor {

    private final MongoAuth plugin;
	private final Database db;
    private final SessionManager sessionManager;
	
	public Changepassword(final MongoAuth instance) {
        this.plugin = instance;
		this.db = instance.getDB();
        this.sessionManager = instance.getSessionManager();
        this.plugin.getCommand("changepassword").setExecutor(this);
        this.plugin.getCommand("changepassword").setTabCompleter(new TabCompleter() {
            @Override
            public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
                return ImmutableList.of();
            }
        });
	}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.hasPermission("mongoauth.user")) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.error_command_permission));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.error_command_sender));
            return true;
        }

        final Account account = db.get(sender.getName());

        if (account == null) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.command_register_hint));
            return true;
        }

        if (!sessionManager.contains(account.getName())) {
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
                    db.save(account);
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