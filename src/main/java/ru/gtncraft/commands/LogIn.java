package ru.gtncraft.commands;

import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.gtncraft.*;

public class LogIn extends SimpleCommand {
	private MongoAuth plugin;
	private Storage storage;
	private Sessions sessions;

    /*private Location findSolidGround(Account player) {
		Location loc = player.getLocation();
		int y = loc.getBlockY();
        int max = 100;

		while (max > 0) {
			loc.setY(y);
			plugin.getLogger().info(String.valueOf(loc.getBlock().getType().getId()));
			if (loc.getBlock().getType().getId() == 0) {
				y = y - 1;
                max--;
			} else {
				return loc;
			}
		}
	}*/
    //Location safe = findSolidGround(player);
    //plugin.getLogger().info(player.getLocation().toString());
    //plugin.getLogger().info(safe.toString());
    //player.teleport(safe);


    public LogIn(MongoAuth instance, Storage storage, Sessions sessions) {
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
            throw new CommandException(Message.PLAYER_NOT_REGISTER + "\n" + Message.REGISTER_COMMAND_HINT);
        }

        if (sessions.contains(sender.getName())) {
            throw new CommandException(Message.PLAYER_IS_LOGGED);
        }

        try {
            String password = args[0];
            if (account.checkPassword(password)) {
                sessions.add(account.getName());
                sender.sendMessage(Message.LOGIN_SUCCESS);
                plugin.getLogger().info("Player " + account + " logged in.");
            } else {
                throw new CommandException(Message.PASSWORD_WRONG);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new CommandException(Message.LOGIN_COMMAND_HINT);
        }
    }
}