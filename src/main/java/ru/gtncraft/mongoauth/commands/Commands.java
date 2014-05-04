package ru.gtncraft.mongoauth.commands;

import ru.gtncraft.mongoauth.MongoAuth;

public class Commands {

    public Commands(final MongoAuth plugin) {
        new Login(plugin);
        new Logout(plugin);
        new ChangePassword(plugin);
        new Register(plugin);
        new Unregister(plugin);
        new Mongoauth(plugin);
    }

}
