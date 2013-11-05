package ru.gtncraft.commands;

import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.gtncraft.*;

public class Unregister extends SimpleCommand {
    private MongoAuth plugin;
	private Storage storage;
	private Sessions sessions;

	public Unregister(MongoAuth instance, Storage storage, Sessions sessions) {
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
            throw new CommandException(Message.PLAYER_NOT_REGISTER);
        }

        if (!sessions.contains(account.getName())) {
            throw new CommandException(Message.LOGIN_COMMAND_HINT);
        }

        try {
            if (account.checkPassword(args[0])) {
                storage.remove(account);
                sessions.remove(account.getName());
                plugin.getLogger().info("Account " + account + " unregistered.");
                sender.sendMessage(Message.UNREGISTER_SUCCESS);
            } else {
                throw new CommandException(Message.PASSWORD_WRONG);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new CommandException(Message.UNREGISTER_COMMAND_HINT);
        }
    }
}
