package ru.gtncraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.gtncraft.*;

public class ChangePassword implements CommandExecutor {

    private final MongoAuth plugin;
	private final Storage storage;
    private final SessionManager sessionManager;
	
	public ChangePassword(final MongoAuth instance) {
        this.plugin = instance;
		this.storage = plugin.getStorage();
        this.sessionManager = plugin.getSessionManager();
        this.plugin.getCommand("cpw").setExecutor(this);
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

        Account account = storage.get(sender.getName());

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
                storage.save(account);
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