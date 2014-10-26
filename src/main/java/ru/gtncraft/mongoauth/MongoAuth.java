package ru.gtncraft.mongoauth;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ru.gtncraft.mongoauth.commands.*;
import ru.gtncraft.mongoauth.database.Database;

import java.io.IOException;
import java.util.regex.Pattern;

public final class MongoAuth extends JavaPlugin implements Listener {
    private final Pattern pattern = Pattern.compile("^[a-zA-Z0-9_]+$");
    private Sessions sessions;
    private Database database;

    @EventHandler(priority = EventPriority.MONITOR)
    @SuppressWarnings("unused")
    public void onPreLogin(final AsyncPlayerPreLoginEvent event) {
        if (!pattern.matcher(event.getName()).matches()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Translations.get(Message.error_input_invalid_login));
        }
    }

    @Override
	public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        saveDefaultConfig();

        try {
            sessions.restore();
        } catch (IOException ignore) {
        }

        String host = getConfig().getString("database.host", "127.0.0.1");
        String dbname = getConfig().getString("database.name", "minecraft");

        try {
            database = new Database(host, dbname);
        } catch (Exception ex) {
            new ListenersEmergency(this);
            return;
        }

        sessions = new Sessions(this);
        new SessionWatcher(this);

        new Listeners(this);

        new Login(this);
        new Logout(this);
        new ChangePassword(this);
        new Register(this);
        new Unregister(this);

        new AutoKick(this);
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        try {
            sessions.save();
        } catch (IOException ignore) {
        }
    }

    public Sessions getSessions() {
        return sessions;
    }

    public Database getDB() {
        return database;
    }
}
