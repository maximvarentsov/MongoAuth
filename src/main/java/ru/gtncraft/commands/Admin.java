package ru.gtncraft.commands;

import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import ru.gtncraft.*;

public class Admin extends SimpleCommand {
    private MongoAuth plugin;
    private Storage storage;
    private Sessions sessions;

    public Admin(MongoAuth instance, Storage storage, Sessions sessions) {
        super("mongoauth.admin");
        this.plugin = instance;
        this.storage = storage;
        this.sessions = sessions;
    }

    @Override
    void execute(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            throw new CommandException();
        }

        switch (args[0].toLowerCase()) {
            case "register":
                try {
                    Account account = storage.get(args[1]);
                    if (account == null) {
                        throw new CommandException(Message.PLAYER_ALREADY_REGISTERED);
                    } else {
                        account.setPassword(args[2]);
                        account.setAllowed(true);
                        account.setIP("127.0.0.1");
                        sender.sendMessage(String.format(Message.ADMIN_SUCCESS_REGISTER_PLAYER, account.getName()));
                        plugin.getLogger().info(String.format("Account %s success register by %s.", account.getName(), sender.getName()));
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    throw new CommandException(Message.ADMIN_MISSING_PLAYERNAME_OR_PASSWORD);
                }
                break;
            case "unregister":
                try {
                    Account account = storage.get(args[1]);
                    if (account == null) {
                        throw new CommandException(Message.PLAYER_NOT_REGISTER);
                    }
                    storage.remove(account);
                    sessions.remove(account.getName());
                    plugin.getLogger().info(sender.getName() + " deleted player " + account + " from database.");
                    sender.sendMessage(String.format(Message.ADMIN_SUCCESS_PLAYER_UREGISTRED, account.getName()));
                } catch (ArrayIndexOutOfBoundsException ex) {
                    throw new CommandException(Message.ADMIN_MISSING_PLAYERNAME);
                }
                break;
            case "cpw":
                try {
                    Account account = storage.get(args[1]);
                    if (account == null) {
                        throw new CommandException(Message.PLAYER_NOT_REGISTER);
                    }
                    account.setPassword(args[2]);
                    storage.save(account);
                    plugin.getLogger().info("Player " + sender.getName() + " changed password for " + account + ".");
                    sender.sendMessage(String.format(Message.ADMIN_SUCCESS_CHANGE_PASSWORD, account.getName()));
                } catch (ArrayIndexOutOfBoundsException ex) {
                    throw new CommandException(Message.ADMIN_MISSING_PLAYERNAME_OR_PASSWORD);
                }
                break;
        }
    }
}