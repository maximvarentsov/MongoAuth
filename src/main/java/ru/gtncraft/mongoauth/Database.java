package ru.gtncraft.mongoauth;

import com.google.common.collect.ImmutableList;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoDatabaseOptions;
import com.mongodb.client.model.CreateIndexOptions;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.configuration.RootCodecRegistry;
import org.bukkit.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Database implements AutoCloseable {
    private final MongoCollection<Account> players;
    private final MongoClient client;

	public Database(final MongoAuth plugin) throws IOException {
        List<ServerAddress> hosts = new ArrayList<ServerAddress>();
        for (String host: plugin.getConfig().getStringList("database.hosts")) {
            hosts.add(new ServerAddress(host));
        }
        client = new MongoClient(
                hosts,
                MongoClientOptions.builder().sslEnabled(plugin.getConfig().getBoolean("database.ssl", false)).build()
        );
        List<CodecProvider> codecs = Arrays.asList(new DocumentCodecProvider(),
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
        MongoDatabaseOptions options = MongoDatabaseOptions.builder().codecRegistry(new RootCodecRegistry(codecs)).
                readPreference(ReadPreference.nearest()).writeConcern(WriteConcern.SAFE).build();
        MongoDatabase db = client.getDatabase(plugin.getConfig().getString("database.name", "minecraft"), options);
        players = db.getCollection(plugin.getConfig().getString("database.collection", "players"), Account.class);
        players.createIndex(new Document("ip", 1));
        players.createIndex(new Document("uuid", new CreateIndexOptions().unique(true)));
	}

    public Account findOne(UUID id) {
        return players.find(new Document("uuid", id.toString())).first();
    }

    public Account remove(Account account) {
        return players.findOneAndDelete(new Document("uuid", account.getId().toString()));
    }

    public void save(final Account account) {
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
