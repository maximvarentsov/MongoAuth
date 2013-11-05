package ru.gtncraft.commands;

import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.gtncraft.Account;
import ru.gtncraft.Message;
import ru.gtncraft.MongoAuth;
import ru.gtncraft.Sessions;

public class LogOut extends SimpleCommand {
    private MongoAuth plugin;
	private Sessions sessions;
	
	public LogOut(MongoAuth instance, Sessions sessions) {
        super("mongoauth.user");
        this.plugin = instance;
		this.sessions = sessions;
	}

    @Override
    void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException(Message.SENDER_NOT_VALID);
        }

        Account account = new Account((Player) sender);

        if (sessions.contains(account.getName())) {
            sessions.remove(account.getName());
            plugin.getLogger().info("Player " + account + " logget out.");
            sender.sendMessage(Message.LOGOUT_SUCCESS);
        } else {
            throw new CommandException(Message.LOGIN_COMMAND_HINT);
        }
    }
}