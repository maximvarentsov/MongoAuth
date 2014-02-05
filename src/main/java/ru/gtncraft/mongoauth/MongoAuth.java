package ru.gtncraft.mongoauth;

import org.bukkit.plugin.java.JavaPlugin;
import ru.gtncraft.mongoauth.commands.*;
import ru.gtncraft.mongoauth.database.Database;
import ru.gtncraft.mongoauth.database.MongoDB;

import java.io.IOException;

public final class MongoAuth extends JavaPlugin {

    private Database db;
    private SessionManager sessionManager;
    private Config config;

    @Override
	public void onEnable() {
        saveDefaultConfig();

        config = new Config(super.getConfig());
        sessionManager = new SessionManager(this);

        if (getConfig().getBoolean("general.restoreSessions")) {
            sessionManager.load();
        }

        try {
			db = new MongoDB(this);
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
        getServer().getScheduler().cancelTasks(this);
    }

    @Override
    public Config getConfig() {
        return config;
    }

    public Database getDB() {
        return db;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
