package ru.gtncraft.mongoauth.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.*;
import ru.gtncraft.mongoauth.database.Database;

import java.util.List;

public class Unregister implements CommandExecutor {

    private final MongoAuth plugin;
	private final Database db;
	private final SessionManager sessionManager;

	public Unregister(final MongoAuth instance) {
        this.plugin = instance;
		this.db = instance.getDB();
		this.sessionManager = instance.getSessionManager();
        this.plugin.getCommand("unregister").setExecutor(this);
        this.plugin.getCommand("unregister").setTabCompleter(new TabCompleter() {
            @Override
            public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
                return ImmutableList.of();
            }
        });
	}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.hasPermission(Permissions.use)) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.error_command_permission));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.error_command_sender));
            return true;
        }

        final Account account = db.get(sender.getName());

        if (account == null) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.error_account_not_registred));
            return true;
        }

        if (!sessionManager.contains(account.getName())) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.command_login_hint));
            return true;
        }

        try {
            final String password = args[0];
            if (account.checkPassword(password)) {
                db.remove(account);
                sessionManager.remove(sender.getName());
                plugin.getLogger().info("Account " + account + " unregistered.");
                sender.sendMessage(plugin.getConfig().getMessage(Messages.success_account_delete));
            } else {
                sender.sendMessage(plugin.getConfig().getMessage(Messages.error_input_password_missmach));
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.error_input_password));
        }
        return true;
    }
}
