package ru.gtncraft.mongoauth.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.Account;
import ru.gtncraft.mongoauth.Message;
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
            sender.sendMessage(Message.PERMISSION_FORBIDDEN);
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.SENDER_NOT_VALID);
            return true;
        }

        Account account = db.get(sender.getName());

        if (account == null) {
            sender.sendMessage(Message.REGISTER_COMMAND_HINT);
            return true;
        }

        if (!sessionManager.contains(account.getName())) {
            sender.sendMessage(Message.LOGIN_COMMAND_HINT);
            return true;
        }

        try {
            String currentPassword = args[0];
            String newPassword = args[1];
            if (account.checkPassword(currentPassword)) {
                account.setPassword(newPassword);
                db.save(account);
                plugin.getLogger().info("Player " + account + " has changed password.");
                sender.sendMessage(Message.CPW_SUCCESS);
            } else {
                sender.sendMessage(Message.PASSWORD_WRONG);
            }
        } catch (Exception ex) {
            sender.sendMessage(Message.CPW_COMMAND_HINT);
        }
        return true;
    }
}