package ru.gtncraft.mongoauth.database;

import ru.gtncraft.mongoauth.Account;

public interface Database extends AutoCloseable {

    Account get(final String playername);
    void remove(final Account document);
    void save(final Account document);
    int countIp(final int ip);

}
