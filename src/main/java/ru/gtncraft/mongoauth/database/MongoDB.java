package ru.gtncraft.mongoauth.database;

import com.google.common.collect.ImmutableList;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.operation.Index;
import org.mongodb.Document;
import ru.gtncraft.mongoauth.Account;
import ru.gtncraft.mongoauth.MongoAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MongoDB implements Database {
    private final MongoCollection players;
    private final MongoClient client;

	public MongoDB(final MongoAuth plugin) throws IOException {
        List<ServerAddress> hosts = new ArrayList<>();
        for (String host: plugin.getConfig().getStringList("database.hosts")) {
            hosts.add(new ServerAddress(host));
        }
        client = new MongoClient(
                hosts,
                MongoClientOptions.builder().sslEnabled(plugin.getConfig().getBoolean("database.ssl", false)).build()
        );
        MongoDatabase db = client.getDatabase(plugin.getConfig().getString("database.name", "minecraft"));
        players = db.getCollection(plugin.getConfig().getString("database.collection", "players"));
        players.tools().createIndexes(ImmutableList.of(
            Index.builder().addKey("uuid").unique().build(),
            Index.builder().addKey("ip").build()
        ));
	}

    public Account get(final UUID uuid) {
        Object obj = players.find(new Document("uuid", uuid.toString())).getOne();
        if (obj != null) {
            return new Account((Document) obj);
        }
        return null;
    }

    public void remove(final Account account) {
        players.find(new Document("uuid", account.getUUID().toString())).removeOne();
    }

    public void save(final Account account) {
        players.insert(account.toDocument());
    }

    public long countIp(final long ip) {
        return players.find(new Document("ip", ip)).count();
    }

    @Override
    public void close() throws Exception {
        client.close();
    }
}
