package ru.gtncraft.mongoauth.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import ru.gtncraft.mongoauth.*;
import ru.gtncraft.mongoauth.database.Database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Mongoauth implements CommandExecutor {

    private final MongoAuth plugin;
    private final Database db;
    private final SessionManager sessionManager;

    public Mongoauth(final MongoAuth instance) {
        this.plugin = instance;
        this.db = instance.getDB();
        this.sessionManager = instance.getSessionManager();
        this.plugin.getCommand("mongoauth").setExecutor(this);
        this.plugin.getCommand("mongoauth").setTabCompleter(new TabCompleter() {

            private final List<String> subs = ImmutableList.of(
                "register", "unregister", "cpw", "changepassword", "changepass", "block"
            );
            private final List<String> bool = ImmutableList.of("true", "false");

            @Override
            public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {

                String lastarg = args[args.length - 1];

                if (args.length <= 1) {
                    return partial(args[0], subs);
                } else if (args.length == 2) {
                    return null;
                } else if (args.length == 3) {
                    String sub = args[0];
                    if ("block".equals(sub)) {
                        return partial(lastarg, bool);
                    }
                }

                return ImmutableList.of();
            }

            private List<String> partial(String token, Collection<String> from) {
                return StringUtil.copyPartialMatches(token, from, new ArrayList<String>(from.size()));
            }
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (args.length == 0) {
            return false;
        }

        if (!sender.hasPermission(Permissions.admin)) {
            sender.sendMessage(plugin.getConfig().getMessage(Messages.error_command_permission));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "register":
                try {
                    final String playername = args[1];
                    final Account account = db.get(playername);
                    if (account != null) {
                        sender.sendMessage(plugin.getConfig().getMessage(Messages.error_account_exists));
                        return true;
                    }
                    try {
                        final String password = args[2];
                        account.setPassword(password);
                        account.setAllowed(true);
                        if (sender instanceof Player) {
                            account.setIP(((Player) sender).getAddress().getAddress().getHostAddress());
                        } else {
                            account.setIP("127.0.0.1");
                        }
                        sender.sendMessage(plugin.getConfig().getMessage(Messages.success_command_admin_register, sender.getName()));
                        plugin.getLogger().info(String.format("Account %s success register by %s.", account.getName(), sender.getName()));
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        sender.sendMessage(plugin.getConfig().getMessage(Messages.error_input_password));
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    sender.sendMessage(plugin.getConfig().getMessage(Messages.error_input_playername));
                }
                break;
            case "unregister":
                try {
                    final Account account = db.get(args[1]);
                    if (account == null) {
                        sender.sendMessage(plugin.getConfig().getMessage(Messages.error_account_not_registred));
                        return true;
                    }
                    db.remove(account);
                    sessionManager.remove(account.getName());
                    sender.sendMessage(plugin.getConfig().getMessage(Messages.success_command_admin_delete, sender.getName()));
                    plugin.getLogger().info(sender.getName() + " deleted player " + account + " from database.");
                } catch (ArrayIndexOutOfBoundsException ex) {
                    sender.sendMessage(plugin.getConfig().getMessage(Messages.error_input_playername));
                }
                break;
            case "changepassword":
                try {
                    final String playername = args[1];
                    final Account account = db.get(playername);
                    if (account == null) {
                        sender.sendMessage(plugin.getConfig().getMessage(Messages.error_account_not_registred));
                        return true;
                    }
                    try {
                        final String password = args[2];
                        account.setPassword(password);
                        db.save(account);
                        plugin.getLogger().info("Player " + sender.getName() + " changed password for " + account + ".");
                        sender.sendMessage(plugin.getConfig().getMessage(Messages.success_command_admin_changepassword, sender.getName()));
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        sender.sendMessage(plugin.getConfig().getMessage(Messages.error_input_password));
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    sender.sendMessage(plugin.getConfig().getMessage(Messages.error_input_playername));
                }
                break;
            default:
                return false;
        }
        return true;
    }
}