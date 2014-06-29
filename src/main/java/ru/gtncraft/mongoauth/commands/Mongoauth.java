package ru.gtncraft.mongoauth.commands;

/*public class Mongoauth extends Command {

    final Collection<String> commands = ImmutableList.of(
        "register", "unregister", "cpw", "changepassword", "changepass", "block"
    );

    public Mongoauth() {
        super("mongoauth");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command pcommand, String s, String[] args) {
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

    @Override
    public Message execute(Player sender, String command, String[] args) {

        if (args.length < 2) {
            return new Message(Messages.error_input_playername);
        }

        switch (args[0].toLowerCase()) {
            case "register":
                return register(sender, args);
            case "unregister":
                return unregister(sender, args);
            case "changepassword":
                return changepassword(sender, args);
            case "block":
                return block(sender, args);
            default:
                return usage();
        }
    }

    Message register(CommandSender sender, String[] args) {

        if (args.length < 3) {
            return new Message(Messages.error_input_password);
        }

        if (getManager().get(uuid) != null) {
            return new Message(Messages.error_account_exists);
        }

        Account newPlayer = new Account(uuid);
        newPlayer.setPassword(args[2]);
        newPlayer.setAllowed(true);
        getManager().save(newPlayer);

        getLogger().info(String.format("Account %s success register by %s.", newPlayer.getUUID(), sender.getName()));

        return new Message(Messages.success_command_admin_register, newPlayer.getUUID());
    }

    Message unregister(CommandSender sender, String[] args) {
        Account account = getManager().get(uuid);

        if (account == null) {
            return new Message(Messages.error_account_not_registred);
        }

        getManager().unregister(account);

        if (getManager().isAuth(uuid)) {
            getManager().logout(uuid);
        }

        getLogger().info(sender.getName() + " deleted player " + args[1] + " from database.");
        return new Message(Messages.success_command_admin_delete, args[1]);

    }

    Message changepassword(CommandSender sender, String[] args) {
        Account account = getManager().get(uuid);

        if (account == null) {
            return new Message(Messages.error_account_not_registred);
        }

        if (args.length < 3) {
            return new Message(Messages.error_input_password);
        }

        account.setPassword(args[2]);
        getManager().save(account);
        getLogger().info("Player " + sender.getName() + " changed password for " + player + ".");

        return new Message(Messages.success_command_admin_changepassword, sender.getName());
    }

    Message block(CommandSender sender, String[] args) {
        if (player == null) {
            return new Message(Messages.error_account_not_registred);
        }

        if (args.length < 3) {
            return new;
        }
        player.setAllowed(false);
        getManager().save(player);
    }

    Message usage() {

    }
}*/

