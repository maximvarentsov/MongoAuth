package ru.gtncraft.mongoauth.database;

import com.google.common.collect.ImmutableList;
import org.mongodb.*;
import ru.gtncraft.mongoauth.Account;
import ru.gtncraft.mongoauth.MongoAuth;

import java.io.IOException;

public class MongoDB implements Database {

    final MongoCollection players;
    final MongoClient client;

	public MongoDB(final MongoAuth plugin) throws IOException {
        client = MongoClients.create(plugin.getConfig().getReplicaSet());
        MongoDatabase db = client.getDatabase(plugin.getConfig().getString("storage.name"));
        players = db.getCollection(plugin.getConfig().getString("storage.collection"));
        players.tools().createIndexes(ImmutableList.of(
            Index.builder().addKey("playername").unique().build(),
            Index.builder().addKey("ip").build()
        ));
	}

    public Account get(final String playername) {
        Object obj = players.find(new Document("playername", playername.toLowerCase())).getOne();
        if (obj != null) {
            return new Account((Document) obj);
        }
        return null;
    }

    public void remove(final Account document) {
        players.find(new Document("playername", document.getName())).removeOne();
    }

    public void save(final Account document) {
        players.find(new Document("playername", document.getName())).upsert().updateOne(document);
    }

    public long countIp(final long ip) {
        return players.find(new Document("ip", ip)).count();
    }

    @Override
    public void close() throws Exception {
        client.close();
    }
}
