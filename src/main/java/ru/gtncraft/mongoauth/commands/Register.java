package ru.gtncraft.mongoauth.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.*;

import java.util.List;

public class Register implements CommandExecutor {

    private final MongoAuth plugin;
	private final Storage storage;
    private final SessionManager sessionManager;
    private final int maxPerIp;
	
	public Register(final MongoAuth instance) {
        this.plugin = instance;
        this.storage = instance.getStorage();
        this.sessionManager = instance.getSessionManager();
        this.maxPerIp = instance.getConfig().getInt("general.maxPerIp", 0);
        this.plugin.getCommand("register").setExecutor(this);
        this.plugin.getCommand("register").setTabCompleter(new TabCompleter() {
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

        Account account = new Account((Player) sender);

        if (sessionManager.contains(account.getName())) {
            sender.sendMessage(Message.PLAYER_IS_LOGGED);
            return true;
        }

        if (storage.get(account.getName()) != null) {
            sender.sendMessage(Message.PLAYER_ALREADY_REGISTERED);
            return true;
        }

        long total = storage.countIp(account.getIP());
        if (maxPerIp > 0 && total >= maxPerIp) {
            sender.sendMessage(Message.REGISTER_LIMIT_REACHED);
            return true;
        }

        try {
            account.setPassword(args[0]);
            storage.save(account);
            sessionManager.add(account.getName());
            plugin.getLogger().info("New player " + account + " registered.");
            sender.sendMessage(Message.REGISTER_SUCCESS);
        } catch (ArrayIndexOutOfBoundsException ex) {
            sender.sendMessage(Message.REGISTER_COMMAND_HINT);
            sender.sendMessage(Message.PASSWORD_MISSING);
        }
        return true;
    }
}
