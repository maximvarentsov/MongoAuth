package ru.gtncraft.mongoauth;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mongodb.Document;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class Account extends Document {

    public Account(final Player player) {
        this.setName(player.getName());
        this.setIP(player);
        this.setAllowed(true);
    }

    public Account(final Map map) {
        this.putAll(map);
    }

    public Account(final String playername) {
        this.setName(playername.toLowerCase());
    }

    public String getName() {
        return getString("playername");
    }

    public void setName(String value) {
        put("playername", value.toLowerCase());
    }

    public long getIP() {
        return getLong("ip");
    }

    public void setIP(final CommandSender commandSender) {

        final String ip;

        if (commandSender instanceof Player) {
            ip = ((Player) commandSender).getAddress().getAddress().getHostAddress();
        } else {
            ip = "127.0.0.1";
        }

        put("ip", dot2LongIP(ip));
    }

    public String getPassword() {
        return getString("password");
    }

    public void setPassword(String value) {
        put("password", encryptPassword(value));
    }

    public boolean isAllowed() {
        return getBoolean("allowed");
    }

    public void setAllowed(final boolean value) {
        put("allowed", value);
    }

    public boolean checkPassword(final String password) {
        return encryptPassword(password).equals(getPassword());
    }

    private static String encryptPassword(final String password) {
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

    private static long dot2LongIP(final String dottedIP) {
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
        return getName();
    }
}
