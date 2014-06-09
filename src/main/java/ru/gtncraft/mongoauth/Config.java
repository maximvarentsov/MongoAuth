package ru.gtncraft.mongoauth;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.mongodb.connection.ServerAddress;

import java.util.List;
import java.util.stream.Collectors;

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
        return getStringList("database.hosts").stream().map(ServerAddress::new).collect(Collectors.toList());
    }

}
