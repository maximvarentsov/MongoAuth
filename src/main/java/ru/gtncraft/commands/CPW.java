package ru.gtncraft.commands;

import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.gtncraft.*;

public class CPW extends SimpleCommand {
    private MongoAuth plugin;
	private Storage storage;
    private Sessions sessions;
	
	public CPW(MongoAuth instance, Storage storage, Sessions sessions) {
        super("mongoauth.user");
        this.plugin = instance;
		this.storage = storage;
        this.sessions = sessions;
	}

    @Override
    void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException(Message.SENDER_NOT_VALID);
        }

        Account account = storage.get(sender.getName());

        if (account == null) {
            throw new CommandException(Message.REGISTER_COMMAND_HINT);
        }

        if (!sessions.contains(account.getName())) {
            throw new CommandException(Message.LOGIN_COMMAND_HINT);
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
                throw new CommandException(Message.PASSWORD_WRONG);
            }
        } catch (Exception ex) {
            throw new CommandException(Message.CPW_COMMAND_HINT);
        }
    }
}