package ru.gtncraft;

import org.bukkit.plugin.java.JavaPlugin;
import ru.gtncraft.commands.*;

public final class MongoAuth extends JavaPlugin {

    private Storage storage;
    private Sessions sessions;

    @Override
	public void onEnable() {
        saveDefaultConfig();

        sessions = new Sessions(this);
        if (getConfig().getConfigurationSection("general").getBoolean("restoreSessions")) {
            sessions.load();
        }

        try {
			storage = new Storage(getConfig().getConfigurationSection("database"));
		} catch (Exception ex) {
			getLogger().severe("Can't connect to MongoDB instance.");
		}

        new Listener(this, storage, sessions);

        getCommand("login").setExecutor(new LogIn(this, storage, sessions));
		getCommand("logout").setExecutor(new LogOut(this, sessions));
		getCommand("register").setExecutor(new Register(this, storage, sessions));
		getCommand("unregister").setExecutor(new Unregister(this, storage, sessions));
		getCommand("cpw").setExecutor(new CPW(this, storage, sessions));
		getCommand("authadmin").setExecutor(new Admin(this, storage, sessions));
	}

    @Override
    public void onDisable() {
        sessions.save();
    }
}
