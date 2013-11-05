package ru.gtncraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.gtncraft.Message;

abstract class SimpleCommand implements CommandExecutor {

    final private String permission;

    public SimpleCommand(String permission) {
        this.permission = permission;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + Message.PERMISSION_FORBIDDEN);
            return false;
        }
        try {
            execute(sender, args);
        } catch (CommandException ex) {
            sender.sendMessage(ChatColor.RED + ex.getMessage());
        }
        return true;
    }

    abstract void execute(CommandSender sender, String[] args) throws CommandException;
}
