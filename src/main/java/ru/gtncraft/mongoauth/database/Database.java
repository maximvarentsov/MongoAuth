package ru.gtncraft.mongoauth.database;

import ru.gtncraft.mongoauth.Account;

import java.util.UUID;

public interface Database extends AutoCloseable {

    Account get(final UUID uuid);
    void remove(final Account document);
    void save(final Account document);
    long countIp(final long ip);

}
