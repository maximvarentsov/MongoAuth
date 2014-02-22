package ru.gtncraft.mongoauth.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.command.*;
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

        if (args.length < 2) {
            sender.sendMessage(config.getMessage(Messages.error_input_playername));
            return false;
        }

        final Account player = authManager.get(args[1]);

        switch (args[0].toLowerCase()) {

            case "register":
                if (player != null) {
                    sender.sendMessage(config.getMessage(Messages.error_account_exists));
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(config.getMessage(Messages.error_input_password));
                    return true;
                }
                Account newPlayer = new Account(args[1]);
                newPlayer.setPassword(args[2]);
                newPlayer.setAllowed(true);
                newPlayer.setIP(sender);
                authManager.save(newPlayer);
                sender.sendMessage(config.getMessage(Messages.success_command_admin_register, newPlayer.getName()));
                logger.info(String.format("Account %s success register by %s.", newPlayer.getName(), sender.getName()));
                return true;

            case "unregister":
                if (player == null) {
                    sender.sendMessage(config.getMessage(Messages.error_account_not_registred));
                    return true;
                }
                authManager.unregister(player);
                if (authManager.isAuth(player.getName())) {
                    authManager.logout(player.getName());
                }
                sender.sendMessage(config.getMessage(Messages.success_command_admin_delete, player.getName()));
                logger.info(sender.getName() + " deleted player " + player + " from database.");
                return true;

            case "changepassword":
                if (player == null) {
                    sender.sendMessage(config.getMessage(Messages.error_account_not_registred));
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(config.getMessage(Messages.error_input_password));
                    return true;
                }
                player.setPassword(args[2]);
                authManager.save(player);
                logger.info("Player " + sender.getName() + " changed password for " + player + ".");
                sender.sendMessage(config.getMessage(Messages.success_command_admin_changepassword, sender.getName()));
                return true;

            case "block":
                if (player == null) {
                    sender.sendMessage(config.getMessage(Messages.error_account_not_registred));
                    return true;
                }
                if (args.length < 3) {
                    return false;
                }
                player.setAllowed(false);
                authManager.save(player);
                return true;
        }

        return false;
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