package ru.gtncraft.mongoauth;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mongodb.Document;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;

public class Account extends Document {

    public Account(final Player player) {
        this.setUUID(player.getUniqueId());
        this.setIP(player);
        this.setAllowed(true);
        this.setName(player);
    }

    public Account(final Map<String, Object> map) {
        this.putAll(map);
    }

    public String getUUID() {
        return getString("uuid");
    }

    @Deprecated
    void setName(final Player player) {
        put("playername", player.getName().toLowerCase());
    }

    void setUUID(final UUID uuid) {
        put("uuid", uuid.toString());
    }

    public long getIP() {
        return getLong("ip");
    }

    void setIP(final CommandSender commandSender) {
        put("ip", getIP(commandSender));
    }

    long getIP(final CommandSender commandSender) {
        final String ip;

        if (commandSender instanceof Player) {
            ip = ((Player) commandSender).getAddress().getAddress().getHostAddress();
        } else {
            ip = "127.0.0.1";
        }

        return dot2LongIP(ip);
    }

    public String getPassword() {
        return getString("password");
    }

    public void setPassword(String value) {
        put("password", encryptPassword(value));
    }

    public boolean isBlocked() {
        return ! getBoolean("allowed");
    }

    public void setAllowed(boolean value) {
        put("allowed", value);
    }

    public boolean checkPassword(final String password) {
        return encryptPassword(password).equals(getPassword());
    }

    static String encryptPassword(final String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder encrypted = new StringBuilder();
            for (byte aHash : hash) {
                String hex = Integer.toHexString(0xff & aHash);
                if (hex.length() == 1) {
                    encrypted.append('0');
                }
                encrypted.append(hex);
            }
            return encrypted.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            return null;
        }
    }

    static long dot2LongIP(final String dottedIP) {
        String[] addrArray = dottedIP.split("\\.");
        long num = 0;
        for (int i = 0; i < addrArray.length; i++) {
            int power = 3 - i;
            num += ((Integer.parseInt(addrArray[i]) % 256) * Math.pow(256, power));
        }
        return num;
    }

    @Override
    public String toString() {
        return getUUID();
    }
}
