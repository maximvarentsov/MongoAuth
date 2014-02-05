package ru.gtncraft.mongoauth;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

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
}
