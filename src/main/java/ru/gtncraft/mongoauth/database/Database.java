package ru.gtncraft.mongoauth.database;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CreateIndexOptions;

import org.bson.Document;
import org.bson.codecs.*;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.configuration.RootCodecRegistry;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class Database implements AutoCloseable {
    private final MongoCollection<Account> players;
    private final MongoClient client;

	public Database(String host, String database) throws Exception {
        MongoClientOptions options = MongoClientOptions.builder().codecRegistry(
                new RootCodecRegistry(
                        Arrays.asList(
                                new ValueCodecProvider(),
                                new DocumentCodecProvider(),
                                new CodecProvider() {
                                    @Override
                                    @SuppressWarnings("unchecked")
                                    public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
                                        if (clazz.equals(Account.class)) {
                                            return (Codec<T>) new AccountCodec();
                                        }
                                        return null;
                                    }
                                }
                        )
                )
        ).build();

        client = new MongoClient(host, options);

        players = client.getDatabase(database).getCollection("players", Account.class);

        try {
            players.createIndex(new Document("login", 1), new CreateIndexOptions().unique(true));
            players.createIndex(new Document("ip", 1));
        } catch (Exception ignore) {
        }
    }

    public Account getAccount(Player player) {
        Document query = new Document("login", player.getName().toLowerCase());
        return players.find(query).first();
    }

    public Account deleteAccount(Account account) {
        Document query = new Document("login", account.getLogin());
        return players.find(query).first();
    }

    public void saveAccount(Account account) {
        Document filter = new Document("login", account.getLogin());
        players.updateOne(filter, account);
    }

    public void createAccount(Account account) {
        players.insertOne(account);
    }

    public long countIp(long ip) {
        return players.count(new Document("ip", ip));
    }

    @Override
    public void close() throws Exception {
        client.close();
    }
}
