package ru.gtncraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.gtncraft.*;

public class Login implements CommandExecutor {

    private final MongoAuth plugin;
	private final Storage storage;
	private final SessionManager sessionManager;

    public Login(final MongoAuth instance) {
        this.plugin = instance;
		this.storage = instance.getStorage();
		this.sessionManager = instance.getSessionManager();
        this.plugin.getCommand("login").setExecutor(this);
	}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.SENDER_NOT_VALID);
            return true;
        }

        if (!sender.hasPermission("mongoauth.user")) {
            sender.sendMessage(Message.PERMISSION_FORBIDDEN);
            return true;
        }

        Account account = storage.get(sender.getName());

        if (account == null) {
            sender.sendMessage(Message.PLAYER_NOT_REGISTER + "\n" + Message.REGISTER_COMMAND_HINT);
            return true;
        }

        if (sessionManager.contains(sender.getName())) {
            sender.sendMessage(Message.PLAYER_IS_LOGGED);
            return true;
        }

        try {
            String password = args[0];
            if (account.checkPassword(password)) {
                sessionManager.add(account.getName());
                sender.sendMessage(Message.LOGIN_SUCCESS);
                plugin.getLogger().info("Player " + account + " logged in.");
            } else {
                sender.sendMessage(Message.PASSWORD_WRONG);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            sender.sendMessage(Message.LOGIN_COMMAND_HINT);
        }
        return true;
    }
}