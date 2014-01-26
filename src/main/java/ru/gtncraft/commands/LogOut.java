package ru.gtncraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.gtncraft.Account;
import ru.gtncraft.Message;
import ru.gtncraft.MongoAuth;
import ru.gtncraft.SessionManager;

public class Logout implements CommandExecutor {

    private final MongoAuth plugin;
	private final SessionManager sessionManager;
	
	public Logout(final MongoAuth instance) {
        this.plugin = instance;
		this.sessionManager = instance.getSessionManager();
        this.plugin.getCommand("logout").setExecutor(this);
	}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.SENDER_NOT_VALID);
            return true;
        }

        Account account = new Account((Player) sender);

        if (sessionManager.remove(account.getName())) {
            plugin.getLogger().info("Player " + account + " logget out.");
            sender.sendMessage(Message.LOGOUT_SUCCESS);
        } else {
            sender.sendMessage(Message.LOGIN_COMMAND_HINT);
        }
        return true;
    }
}