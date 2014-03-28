package ru.gtncraft.mongoauth.database;

import ru.gtncraft.mongoauth.Account;

public interface Database extends AutoCloseable {

    Account get(final String playername);
    void remove(final Account document);
    void save(final Account document);
    long countIp(final long ip);

}
