package ru.gtncraft.mongoauth.database;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabaseOptions;
import com.mongodb.client.model.CreateIndexOptions;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.configuration.RootCodecRegistry;
import ru.gtncraft.mongoauth.Security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Database implements AutoCloseable {
    private final static List<CodecProvider> codecs;
    private final MongoCollection<Account> players;
    private final MongoCollection<Log> logs;
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
                        if (clazz.equals(Log.class)) {
                            return (Codec<T>) new LogCodec();
                        }
                        return null;
                    }
                }
        );
    }

	public Database(String host, String database) throws IOException {
        MongoDatabaseOptions options = MongoDatabaseOptions.builder().
                                       codecRegistry(new RootCodecRegistry(codecs)).
                                       readPreference(ReadPreference.nearest()).
                                       writeConcern(WriteConcern.SAFE).build();
        client = new MongoClient(host);

        players = client.getDatabase(database, options).getCollection("players", Account.class);
        players.createIndex(new Document("uuid", new CreateIndexOptions().unique(true)));
        players.createIndex(new Document("ip", 1));

        logs = client.getDatabase(database, options).getCollection("logs", Log.class);
        logs.createIndex(new Document("uuid", 1));
        logs.createIndex(new Document("ip", 1));
        logs.createIndex(new Document("status", 1));
    }

    public Account getAccount(UUID id) {
        return players.find(new Document("uuid", id.toString())).first();
    }

    public Account deleteAccount(Account account) {
        return players.findOneAndDelete(new Document("uuid", account.getId().toString()));
    }

    public void saveAccount(Account account) {
        players.insertOne(account);
    }

    public long countIp(long ip) {
        return players.count(new Document("ip", ip));
    }

    public void log(UUID id, long ip, Log.Status status) {
        logs.insertOne(new Log(id, ip, status));
    }

    public long countAttempts(UUID id) {
        Document query = new Document("uuid", id.toString());
        query.append("status", Log.Status.BAD_LOGIN.getIntRepresentation());

        Document between = new Document();
        Date gte = new Date();
        gte.setMinutes(gte.getMinutes() - Security.minutes);
        between.append("$gte", gte);
        between.append("$lte", new Date());

        query.append("date", between);

        return logs.count(query);
    }

    @Override
    public void close() throws Exception {
        client.close();
    }
}
