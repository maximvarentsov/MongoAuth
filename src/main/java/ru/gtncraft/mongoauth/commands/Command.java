package ru.gtncraft.mongoauth.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.Account;
import ru.gtncraft.mongoauth.Config;
import ru.gtncraft.mongoauth.Messages;
import ru.gtncraft.mongoauth.MongoAuth;
import ru.gtncraft.mongoauth.manager.AuthManager;

import java.util.List;
import java.util.logging.Logger;

abstract class Command implements CommandExecutor, TabCompleter {

    private MongoAuth plugin;

    class Message {

        final Messages message;
        final String[] args;

        public Message(Messages messages, String... args) {
            this.message = messages;
            this.args = args;
        }

        public Messages getMessage() {
            return message;
        }

        public String[] getArgs() {
            return args;
        }
    }

    public Command(MongoAuth plugin) {
        this.plugin = plugin;
    }

    public MongoAuth getPlugin() {
        return plugin;
    }

    public Logger getLogger() {
        return getPlugin().getLogger();
    }

    public Config getConfig() {
        return getPlugin().getConfig();
    }

    public String getMessage(Messages key, String...args) {
        return getConfig().getMessage(key, args);
    }

    public AuthManager getManager() {
        return getPlugin().getAuthManager();
    }

    public Account getAccount(final Player player) {
        return getManager().get(player.getUniqueId());
    }

    public boolean isAuthorized(final Player player) {
        return getManager().isAuth(player.getUniqueId());
    }

    public boolean logout(final Player player) {
        return getManager().logout(player.getUniqueId());
    }

    public abstract Message execute(Player player, String command, String[] args);

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String s, String[] strings) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(getMessage(Messages.error_command_sender));
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Message message = execute((Player) sender, s, strings);
            sender.sendMessage(getMessage(message.getMessage(), message.getArgs()));
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String s, String[] strings) {
        return ImmutableList.of();
    }
}
