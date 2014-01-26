package ru.gtncraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.gtncraft.*;

public class Create implements CommandExecutor {

    private final MongoAuth plugin;
    private final Storage storage;
    private final SessionManager sessionManager;

    public Create(final MongoAuth instance) {
        this.plugin = instance;
        this.storage = instance.getStorage();
        this.sessionManager = instance.getSessionManager();
        this.plugin.getCommand("create").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (args.length == 0) {
            return false;
        }

        if (!sender.hasPermission("mongoauth.admin")) {
            sender.sendMessage(Message.PERMISSION_FORBIDDEN);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "register":
                try {
                    Account account = storage.get(args[1]);
                    if (account == null) {
                        sender.sendMessage(Message.PLAYER_ALREADY_REGISTERED);
                        return true;
                    }
                    account.setPassword(args[2]);
                    account.setAllowed(true);
                    account.setIP("127.0.0.1");
                    sender.sendMessage(String.format(Message.ADMIN_SUCCESS_REGISTER_PLAYER, account.getName()));
                    plugin.getLogger().info(String.format("Account %s success register by %s.", account.getName(), sender.getName()));
                } catch (ArrayIndexOutOfBoundsException ex) {
                    sender.sendMessage(Message.ADMIN_MISSING_PLAYERNAME_OR_PASSWORD);
                    return true;
                }
                break;
            case "unregister":
                try {
                    Account account = storage.get(args[1]);
                    if (account == null) {
                        sender.sendMessage(Message.PLAYER_NOT_REGISTER);
                        return true;
                    }
                    storage.remove(account);
                    sessionManager.remove(account.getName());
                    plugin.getLogger().info(sender.getName() + " deleted player " + account + " from database.");
                    sender.sendMessage(String.format(Message.ADMIN_SUCCESS_PLAYER_UREGISTRED, account.getName()));
                } catch (ArrayIndexOutOfBoundsException ex) {
                    sender.sendMessage(Message.ADMIN_MISSING_PLAYERNAME);
                    return true;
                }
                break;
            case "cpw":
                try {
                    Account account = storage.get(args[1]);
                    if (account == null) {
                        sender.sendMessage(Message.PLAYER_NOT_REGISTER);
                        return true;
                    }
                    account.setPassword(args[2]);
                    storage.save(account);
                    plugin.getLogger().info("Player " + sender.getName() + " changed password for " + account + ".");
                    sender.sendMessage(String.format(Message.ADMIN_SUCCESS_CHANGE_PASSWORD, account.getName()));
                } catch (ArrayIndexOutOfBoundsException ex) {
                    sender.sendMessage(Message.ADMIN_MISSING_PLAYERNAME_OR_PASSWORD);
                    return true;
                }
                break;
        }
        return true;
    }
}