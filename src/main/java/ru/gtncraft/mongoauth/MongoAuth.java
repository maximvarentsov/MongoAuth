package ru.gtncraft.mongoauth;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ru.gtncraft.mongoauth.commands.*;

import java.util.regex.Pattern;

public final class MongoAuth extends JavaPlugin implements Listener {
    private final Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]+$");
    private AuthManager authManager;

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    void onPreLogin(final AsyncPlayerPreLoginEvent event) {
        if (!pattern.matcher(event.getName()).matches()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Translations.get(Message.error_input_invalid_login));
        }
    }

    @Override
	public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
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
