package ru.gtncraft.mongoauth;

import org.bukkit.plugin.java.JavaPlugin;
import ru.gtncraft.mongoauth.commands.*;

public final class MongoAuth extends JavaPlugin {
    private AuthManager authManager;

    @Override
	public void onEnable() {
        saveDefaultConfig();
        Messages.init(getConfig().getConfigurationSection("messages"));
        try {
            authManager = new AuthManager(this);
            new Listeners(this);
        } catch (Exception ex) {
            new ListenersEmergency(this);
            ex.printStackTrace();
        } finally {
            new Login(this);
            new Logout(this);
            new ChangePassword(this);
            new Register(this);
            new Unregister(this);
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
