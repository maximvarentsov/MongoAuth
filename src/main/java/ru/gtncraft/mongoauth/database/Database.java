package ru.gtncraft.mongoauth.database;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabaseOptions;
import com.mongodb.client.model.CreateIndexOptions;

import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.configuration.RootCodecRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Database implements AutoCloseable {
    private final static List<CodecProvider> codecs;
    private final MongoCollection<Account> players;
    private final MongoClient client;

    static {
        codecs = Arrays.asList(new DocumentCodecProvider(),
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
        );
    }

	public Database(String host, String database) throws Exception {
        MongoDatabaseOptions options = MongoDatabaseOptions.builder().
                                       codecRegistry(new RootCodecRegistry(codecs)).
                                       readPreference(ReadPreference.nearest()).
                                       writeConcern(WriteConcern.SAFE).build();
        client = new MongoClient(host);
        players = client.getDatabase(database, options).getCollection("players", Account.class);
        players.createIndex(new Document("uuid", new CreateIndexOptions().unique(true)));
        players.createIndex(new Document("ip", 1));
    }

    public Account getAccount(UUID id) {
        return players.find(new Document("uuid", id.toString())).first();
    }

    public Account deleteAccount(UUID id) {
        return players.findOneAndDelete(new Document("uuid", id.toString()));
    }

    public void saveAccount(Account account) {
        players.updateOne(new Document("uuid", account.toString()), account, new UpdateOptions().upsert(true));
    }

    public long countIp(long ip) {
        return players.count(new Document("ip", ip));
    }

    @Override
    public void close() throws Exception {
        client.close();
    }
}
