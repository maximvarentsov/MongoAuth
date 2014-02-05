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

public class Register implements CommandExecutor {

    private final MongoAuth plugin;
	private final Database db;
    private final SessionManager sessionManager;
    private final int maxPerIp;
	
	public Register(final MongoAuth instance) {
        this.plugin = instance;
        this.db = instance.getDB();
        this.sessionManager = instance.getSessionManager();
        this.maxPerIp = instance.getConfig().getInt("general.maxPerIp", 0);
        this.plugin.getCommand("register").setExecutor(this);
        this.plugin.getCommand("register").setTabCompleter(new TabCompleter() {
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

        final Account account = new Account((Player) sender);

        if (sessionManager.contains(account.getName())) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.error_account_is_auth));
            return true;
        }

        if (db.get(account.getName()) != null) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.error_account_exists));
            return true;
        }

        final long total = db.countIp(account.getIP());
        if (maxPerIp > 0 && total >= maxPerIp) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.error_account_register_limit));
            return true;
        }

        try {
            final String password = args[0];
            account.setPassword(password);
            db.save(account);
            sessionManager.add(account.getName());
            plugin.getLogger().info("New player " + account + " registered.");
            sender.sendMessage(plugin.getConfig().getMessage(Messages.success_account_create));
        } catch (ArrayIndexOutOfBoundsException ex) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.error_input_password));
        }
        return true;
    }
}
