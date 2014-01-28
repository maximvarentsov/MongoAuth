package ru.gtncraft.mongoauth.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.*;

import java.util.List;

public class Unregister implements CommandExecutor {

    private final MongoAuth plugin;
	private final Storage storage;
	private final SessionManager sessionManager;

	public Unregister(final MongoAuth instance) {
        this.plugin = instance;
		this.storage = instance.getStorage();
		this.sessionManager = instance.getSessionManager();
        this.plugin.getCommand("unregister").setExecutor(this);
        this.plugin.getCommand("unregister").setTabCompleter(new TabCompleter() {
            @Override
            public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
                return ImmutableList.of();
            }
        });
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
            sender.sendMessage(Message.PLAYER_NOT_REGISTER);
            return true;
        }

        if (!sessionManager.contains(account.getName())) {
            sender.sendMessage(Message.LOGIN_COMMAND_HINT);
            return true;
        }

        try {
            if (account.checkPassword(args[0])) {
                storage.remove(account);
                sessionManager.remove(sender.getName());
                plugin.getLogger().info("Account " + account + " unregistered.");
                sender.sendMessage(Message.UNREGISTER_SUCCESS);
            } else {
                sender.sendMessage(Message.PASSWORD_WRONG);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            sender.sendMessage(Message.UNREGISTER_COMMAND_HINT);
        }
        return true;
    }
}
