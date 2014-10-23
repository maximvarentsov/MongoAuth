package ru.gtncraft.mongoauth;

import org.bukkit.plugin.java.JavaPlugin;
import ru.gtncraft.mongoauth.commands.*;
import ru.gtncraft.mongoauth.manager.AuthManager;

public final class MongoAuth extends JavaPlugin {

    private AuthManager authManager;
    public final String channel = "mongoauth";

    @Override
	public void onEnable() {
        saveDefaultConfig();
        Messages.init(getConfig().getConfigurationSection("messages"));
        try {
            authManager = new AuthManager(this);
            new Listeners(this);
            new Login(this);
            new Logout(this);
            new ChangePassword(this);
            new Register(this);
            new Unregister(this);
        } catch (Exception ex) {
            new ListenersEmergency(this);
            ex.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        authManager.disable();
        getServer().getScheduler().cancelTasks(this);
    }

    public AuthManager getAuthManager() {
        return authManager;
    }
}
