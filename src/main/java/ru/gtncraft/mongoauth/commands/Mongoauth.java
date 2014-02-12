package ru.gtncraft.mongoauth.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import ru.gtncraft.mongoauth.Account;
import ru.gtncraft.mongoauth.AuthManager;
import ru.gtncraft.mongoauth.Messages;
import ru.gtncraft.mongoauth.MongoAuth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Mongoauth implements CommandExecutor {

    private final MongoAuth plugin;
    private final AuthManager authManager;

    public Mongoauth(final MongoAuth instance) {
        this.plugin = instance;
        this.authManager = instance.getAuthManager();

        final PluginCommand command = this.plugin.getCommand("mongoauth");
        command.setExecutor(this);
        command.setPermissionMessage(plugin.getConfig().getMessage(Messages.error_command_permission));
        command.setTabCompleter(new TabCompleter() {

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

        switch (args[0].toLowerCase()) {
            case "register":
                try {
                    final String playername = args[1];
                    final Account account = authManager.get(playername);
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
                    final Account account = authManager.get(args[1]);
                    if (account == null) {
                        sender.sendMessage(plugin.getConfig().getMessage(Messages.error_account_not_registred));
                        return true;
                    }
                    authManager.unregister(account);
                    authManager.logout(account.getName());
                    sender.sendMessage(plugin.getConfig().getMessage(Messages.success_command_admin_delete, sender.getName()));
                    plugin.getLogger().info(sender.getName() + " deleted player " + account + " from database.");
                } catch (ArrayIndexOutOfBoundsException ex) {
                    sender.sendMessage(plugin.getConfig().getMessage(Messages.error_input_playername));
                }
                break;
            case "changepassword":
                try {
                    final String playername = args[1];
                    final Account account = authManager.get(playername);
                    if (account == null) {
                        sender.sendMessage(plugin.getConfig().getMessage(Messages.error_account_not_registred));
                        return true;
                    }
                    try {
                        final String password = args[2];
                        account.setPassword(password);
                        authManager.save(account);
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