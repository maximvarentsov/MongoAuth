package ru.gtncraft.mongoauth.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.*;
import ru.gtncraft.mongoauth.database.Database;

import java.util.List;
import java.util.logging.Logger;

public abstract class Command implements CommandExecutor, TabCompleter {
    private MongoAuth plugin;
    private Database database;

    public Command(MongoAuth plugin) {
        this.plugin = plugin;
        this.database = plugin.getDB();
    }

    public Database getDatabase() {
        return database;
    }

    public Logger getLogger() {
        return plugin.getLogger();
    }

    public Session getSession(Player player) {
        return plugin.getSessions().get(player.getUniqueId());
    }

    public boolean isAuthorized(Player player) {
        return ! plugin.getSessions().notAuthenticated(player.getUniqueId());
    }

    public void logout(Player player) {
        plugin.getSessions().quit(player.getUniqueId());
    }

    public void login(Player player) {
        plugin.getSessions().join(player.getUniqueId());
    }

    public boolean checkRegistrationLimit(long ip) {
        return plugin.getSessions().checkRegistrationLimit(ip);
    }

    public abstract Message execute(Player player, String[] args);

    @Override
    public boolean onCommand(final CommandSender sender, org.bukkit.command.Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Translations.get(Message.error_command_sender));
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                String message = Translations.get(execute((Player) sender, args));
                sender.sendMessage(message);
            }
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String s, String[] strings) {
        return ImmutableList.of();
    }

    public static long dot2LongIP(final String dottedIP) {
        String[] addrArray = dottedIP.split("\\.");
        long num = 0;
        for (int i = 0; i < addrArray.length; i++) {
            int power = 3 - i;
            num += ((Integer.parseInt(addrArray[i]) % 256) * Math.pow(256, power));
        }
        return num;
    }
}
