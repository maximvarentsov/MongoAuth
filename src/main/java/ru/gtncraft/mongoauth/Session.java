package ru.gtncraft.mongoauth;

import ru.gtncraft.mongoauth.database.Account;

import java.io.Serializable;
import java.util.Date;

import static ru.gtncraft.mongoauth.util.Password.encrypt;

public class Session implements Serializable {
    private final Date connected = new Date();
    private int attempts = 0;
    private Account account;

    public Session(Account account) {
        this.account = account;
    }

    public Date getConnected() {
        return connected;
    }

    public boolean isRegister() {
        return account != null;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public boolean checkPassword(String password) {
        return getAccount().getPassword().equals(encrypt(password));
    }

    public int getAttempts() {
        attempts++;
        return attempts;
    }
}
