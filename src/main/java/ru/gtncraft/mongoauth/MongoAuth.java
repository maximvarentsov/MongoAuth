package ru.gtncraft.mongoauth;

import org.bukkit.plugin.java.JavaPlugin;
import ru.gtncraft.mongoauth.commands.*;
import ru.gtncraft.mongoauth.manager.AuthManager;

import java.io.IOException;

public final class MongoAuth extends JavaPlugin {

    private AuthManager authManager;
    private Config config;
    public final String channel = "mongoauth";

    @Override
	public void onEnable() {
        saveDefaultConfig();
        config = new Config(super.getConfig());
        try {
            authManager = new AuthManager(this);
            new Listeners(this);
            new Login(this);
            new Logout(this);
            new ChangePassword(this);
            new Register(this);
            new Unregister(this);
        } catch (IOException ex) {
            new EmergencyListeners(this);
            getLogger().severe("Emergency mode!");
            getLogger().severe(ex.getMessage());
        }
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
