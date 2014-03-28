package ru.gtncraft.mongoauth;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.mongodb.connection.ServerAddress;

import java.util.ArrayList;
import java.util.List;

public class Config extends YamlConfiguration {

    public Config(FileConfiguration config) {
        super();
        this.addDefaults(config.getRoot());
    }

    public String getMessage(final Messages key) {
        return ChatColor.translateAlternateColorCodes('&', this.getString("messages." + key.name()));
    }

    public String getMessage(final Messages key, String...args) {
        return String.format(getMessage(key), args);
    }

    public List<ServerAddress> getReplicaSet() {
        List<ServerAddress> result = new ArrayList<>();
        for (String host : getStringList("storage.hosts")) {
            result.add(new ServerAddress(host));
        }
        return result;
    }

}
