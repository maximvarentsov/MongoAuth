package ru.gtncraft.mongoauth;

import org.bukkit.plugin.java.JavaPlugin;
import ru.gtncraft.mongoauth.commands.*;

public final class MongoAuth extends JavaPlugin {

    AuthManager authManager;
    Config config;

    @Override
	public void onEnable() {
        saveDefaultConfig();

        config = new Config(super.getConfig());
        authManager = new AuthManager(this);

        new Listeners(this);
        new Commands(this);
	}

    @Override
    public void onDisable() {
        authManager.disable();
        getServer().getScheduler().cancelTasks(this);
    }

    @Override
    public Config getConfig() {
        return config;
    }

    public AuthManager getAuthManager() {
        return authManager;
    }
}
