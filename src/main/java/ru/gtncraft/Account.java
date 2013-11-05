package ru.gtncraft;

import com.mongodb.BasicDBObject;
import org.bukkit.entity.Player;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class Account extends BasicDBObject {

    public Account(Player player) {
        this.setName(player.getName());
        this.setIP(player.getAddress().getAddress().getHostAddress());
        this.setAllowed(true);
    }

    public Account(Map map) {
        putAll(map);
    }

    public String getName() {
        return getString("playername");
    }

    public void setName(String value) {
        put("playername", value.toLowerCase());
    }

    public int getIP() {
        return getInt("ip");
    }

    public void setIP(String value) {
        put("ip", dot2LongIP(value));
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

    public void setAllowed(boolean value) {
        put("allowed", value);
    }

    public boolean checkPassword(String password) {
        return getPassword().equals(encryptPassword(password));
    }

    private String encryptPassword(String password) {
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

    private long dot2LongIP(String dottedIP) {
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
