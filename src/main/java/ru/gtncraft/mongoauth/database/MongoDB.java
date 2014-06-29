package ru.gtncraft.mongoauth.database;

import com.google.common.collect.ImmutableList;
import org.mongodb.*;
import ru.gtncraft.mongoauth.Account;
import ru.gtncraft.mongoauth.MongoAuth;

import java.io.IOException;
import java.util.UUID;

public class MongoDB implements Database {

    final MongoCollection players;
    final MongoClient client;

	public MongoDB(final MongoAuth plugin) throws IOException {
        client = MongoClients.create(
                plugin.getConfig().getReplicaSet(),
                MongoClientOptions.builder().SSLEnabled(plugin.getConfig().getBoolean("database.ssl")).build()
        );
        MongoDatabase db = client.getDatabase(plugin.getConfig().getString("database.name"));
        players = db.getCollection(plugin.getConfig().getString("database.collection"));
        players.tools().createIndexes(ImmutableList.of(
            Index.builder().addKey("playername").unique().build(),
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

    @Deprecated
    public Account get(final String player) {
        Object obj = players.find(new Document("playername", player.toLowerCase())).getOne();
        if (obj != null) {
            return new Account((Document) obj);
        }
        return null;
    }

    public void remove(final Account document) {
        players.find(new Document("uuid", document.getUUID())).removeOne();
    }

    public void save(final Account document) {
        //players.find(new Document("uuid", document.getUUID())).upsert().updateOne(document);
        players.insert(document);
    }

    public long countIp(final long ip) {
        return players.find(new Document("ip", ip)).count();
    }

    @Override
    public void close() throws Exception {
        client.close();
    }
}
