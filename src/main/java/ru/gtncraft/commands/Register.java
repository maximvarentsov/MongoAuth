package ru.gtncraft.commands;

import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.gtncraft.*;
import ru.gtncraft.Storage;

public class Register extends SimpleCommand {
    private MongoAuth plugin;
	private Storage storage;
    private Sessions sessions;
    final private int maxPerIp;
	
	public Register(MongoAuth instance, Storage storage, Sessions sessions) {
        super("mongoauth.user");
        this.plugin = instance;
        this.storage = storage;
        this.sessions = sessions;
        this.maxPerIp = instance.getConfig().getConfigurationSection("general").getInt("maxPerIp");
	}

    @Override
    void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException(Message.SENDER_NOT_VALID);
        }

        Account account = new Account((Player) sender);

        if (sessions.contains(account.getName())) {
            throw new CommandException(Message.PLAYER_IS_LOGGED);
        }

        if (storage.get(account.getName()) != null) {
            throw new CommandException(Message.PLAYER_ALREADY_REGISTERED);
        }

        long total = storage.countIp(account.getIP());
        if (maxPerIp > 0 && total >= maxPerIp) {
            throw new CommandException(Message.REGISTER_LIMIT_REACHED);
        }

        try {
            account.setPassword(args[0]);
            storage.save(account);
            sessions.add(account.getName());
            plugin.getLogger().info("New player " + account + " registered.");
            sender.sendMessage(Message.REGISTER_SUCCESS);
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new CommandException(Message.REGISTER_COMMAND_HINT + "\n" + Message.PASSWORD_MISSING);
        }
    }
}
