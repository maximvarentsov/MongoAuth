package ru.gtncraft.mongoauth.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.gtncraft.mongoauth.*;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import static ru.gtncraft.mongoauth.util.Strings.partial;

public class Mongoauth implements CommandExecutor, TabCompleter {

    private final AuthManager authManager;
    private final Config config;
    private final Logger logger;
    private final Collection<String> commands = ImmutableList.of(
        "register", "unregister", "cpw", "changepassword", "changepass", "block"
    );

    public Mongoauth(final MongoAuth plugin) {
        this.authManager = plugin.getAuthManager();
        this.config = plugin.getConfig();
        this.logger = plugin.getLogger();
        final PluginCommand command = plugin.getCommand("mongoauth");
        command.setExecutor(this);
        command.setPermissionMessage(plugin.getConfig().getMessage(Messages.error_command_permission));
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
                        sender.sendMessage(config.getMessage(Messages.error_account_exists));
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
                        sender.sendMessage(config.getMessage(Messages.success_command_admin_register, sender.getName()));
                        logger.info(String.format("Account %s success register by %s.", account.getName(), sender.getName()));
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        sender.sendMessage(config.getMessage(Messages.error_input_password));
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    sender.sendMessage(config.getMessage(Messages.error_input_playername));
                }
                break;
            case "unregister":
                try {
                    final Account account = authManager.get(args[1]);
                    if (account == null) {
                        sender.sendMessage(config.getMessage(Messages.error_account_not_registred));
                        return true;
                    }
                    authManager.unregister(account);
                    authManager.logout(account.getName());
                    sender.sendMessage(config.getMessage(Messages.success_command_admin_delete, sender.getName()));
                    logger.info(sender.getName() + " deleted player " + account + " from database.");
                } catch (ArrayIndexOutOfBoundsException ex) {
                    sender.sendMessage(config.getMessage(Messages.error_input_playername));
                }
                break;
            case "changepassword":
                try {
                    final String playername = args[1];
                    final Account account = authManager.get(playername);
                    if (account == null) {
                        sender.sendMessage(config.getMessage(Messages.error_account_not_registred));
                        return true;
                    }
                    try {
                        final String password = args[2];
                        account.setPassword(password);
                        authManager.save(account);
                        logger.info("Player " + sender.getName() + " changed password for " + account + ".");
                        sender.sendMessage(config.getMessage(Messages.success_command_admin_changepassword, sender.getName()));
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        sender.sendMessage(config.getMessage(Messages.error_input_password));
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    sender.sendMessage(config.getMessage(Messages.error_input_playername));
                }
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command pcommand, String s, String[] args) {
        if (args.length > 1) {
            final String lastarg = args[args.length - 1];
            final String command = args[0].toLowerCase();
            switch (args.length) {
                case 2:
                    return null;
                case 3:
                    if ("block".equals(command)) {
                        return partial(lastarg, ImmutableList.of("true", "false"));
                    }
            }
        } else {
            return partial(args[0], commands);
        }
        return ImmutableList.of();
    }
}