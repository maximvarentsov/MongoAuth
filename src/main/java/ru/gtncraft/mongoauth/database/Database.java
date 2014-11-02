package ru.gtncraft.mongoauth.database;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCollectionOptions;
import com.mongodb.client.MongoDatabaseOptions;
import com.mongodb.client.model.CreateIndexOptions;

import com.mongodb.client.model.UpdateOptions;
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

    static class AccountCodecProvider implements CodecProvider {

        public AccountCodecProvider() {
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
            if (clazz.equals(Account.class)) {
                System.out.println("xx");
                return (Codec<T>) new AccountCodec();
            }
            return null;
        }
    }

	public Database(String host, String database) throws Exception {

        MongoClientOptions options = MongoClientOptions.builder().codecRegistry(
                new RootCodecRegistry(
                        Arrays.asList(
                            new ValueCodecProvider(),
                            new DocumentCodecProvider(),
                            new AccountCodecProvider()
                    )
                )
        ).build();

        client = new MongoClient(host, options);

        players = client.getDatabase(database).getCollection("players", Account.class);

        try {
            players.createIndex(new Document("login", 1), new CreateIndexOptions().unique(true));
            players.createIndex(new Document("ip", 1));
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    public Account getAccount(Player player) {
        Document query = new Document("login", player.getName().toLowerCase());
        return players.find(query).first();
    }

    public Account deleteAccount(Player player) {
        Document query = new Document("login", player.getName().toLowerCase());
        return players.findOneAndDelete(query);
    }

    public void saveAccount(Account account) {
        Document query = new Document("login", account.getLogin().toLowerCase());
        players.updateOne(query, account, new UpdateOptions().upsert(true));
    }

    public long countIp(long ip) {
        return players.count(new Document("ip", ip));
    }

    @Override
    public void close() throws Exception {
        client.close();
    }
}
