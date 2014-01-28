package ru.gtncraft.mongoauth;

import org.bukkit.plugin.java.JavaPlugin;
import ru.gtncraft.mongoauth.commands.*;

import java.io.IOException;

public final class MongoAuth extends JavaPlugin {

    private Storage storage;
    private SessionManager sessionManager;

    @Override
	public void onEnable() {
        saveDefaultConfig();

        sessionManager = new SessionManager(this);
        if (getConfig().getBoolean("general.restoreSessions")) {
            sessionManager.load();
        }

        try {
			storage = new Storage(this);
		} catch (IOException ex) {
			getLogger().severe("Can't connect to MongoDB instance.");
		}

        new Listeners(this);
        new Login(this);
        new Logout(this);
        new Changepassword(this);
        new Register(this);
        new Unregister(this);
        new Mongoauth(this);
	}

    @Override
    public void onDisable() {
        sessionManager.save();
    }

    public Storage getStorage() {
        return storage;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
