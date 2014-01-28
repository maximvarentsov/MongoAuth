package ru.gtncraft.mongoauth.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.Account;
import ru.gtncraft.mongoauth.Message;
import ru.gtncraft.mongoauth.MongoAuth;
import ru.gtncraft.mongoauth.SessionManager;

import java.util.List;

public class Logout implements CommandExecutor {

    private final MongoAuth plugin;
	private final SessionManager sessionManager;
	
	public Logout(final MongoAuth instance) {
        this.plugin = instance;
		this.sessionManager = instance.getSessionManager();
        this.plugin.getCommand("logout").setExecutor(this);
        this.plugin.getCommand("logout").setTabCompleter(new TabCompleter() {
            @Override
            public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
                return ImmutableList.of();
            }
        });
	}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.SENDER_NOT_VALID);
            return true;
        }

        Account account = new Account((Player) sender);

        if (sessionManager.remove(account.getName())) {
            plugin.getLogger().info("Player " + account + " logget out.");
            sender.sendMessage(Message.LOGOUT_SUCCESS);
        } else {
            sender.sendMessage(Message.LOGIN_COMMAND_HINT);
        }
        return true;
    }
}