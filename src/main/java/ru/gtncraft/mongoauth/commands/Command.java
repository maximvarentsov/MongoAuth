package ru.gtncraft.mongoauth.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.Account;
import ru.gtncraft.mongoauth.Message;
import ru.gtncraft.mongoauth.Messages;
import ru.gtncraft.mongoauth.MongoAuth;
import ru.gtncraft.mongoauth.AuthManager;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Logger;

abstract class Command implements CommandExecutor, TabCompleter {
    private MongoAuth plugin;

    public Command(MongoAuth plugin) {
        this.plugin = plugin;
    }

    public MongoAuth getPlugin() {
        return plugin;
    }

    public Logger getLogger() {
        return getPlugin().getLogger();
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
    public boolean onCommand(final CommandSender sender, org.bukkit.command.Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.get(Message.error_command_sender));
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                String message = Messages.get(execute((Player) sender, s, args));
                sender.sendMessage(message);
            }
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String s, String[] strings) {
        return ImmutableList.of();
    }

    public static String encryptPassword(final String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder encrypted = new StringBuilder();
            for (byte aHash : hash) {
                String hex = Integer.toHexString(0xff & aHash);
                if (hex.length() == 1) {
                    encrypted.append('0');
                }
                encrypted.append(hex);
            }
            return encrypted.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ignore) {
            return null;
        }
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
