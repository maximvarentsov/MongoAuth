package ru.gtncraft.mongoauth.database;

import com.mongodb.*;
import ru.gtncraft.mongoauth.Account;
import ru.gtncraft.mongoauth.MongoAuth;

import java.io.IOException;

public class MongoDB implements Database {

    private final DBCollection players;

	public MongoDB(final MongoAuth plugin) throws IOException {
        MongoClient mongoClient = new MongoClient(
                plugin.getConfig().getString("database.host", "localhost"),
                plugin.getConfig().getInt("database.port", 27017)
        );
        DB db = mongoClient.getDB(plugin.getConfig().getString("database.name", "minecraft"));
        players = db.getCollection(plugin.getConfig().getString("database.collection", "players"));
        if (players.count() < 1) {
            ensureIndex();
        }
	}

    public Account get(final String playername) {
        DBObject obj = players.findOne(new BasicDBObject("playername", playername.toLowerCase()));
        if (obj == null) {
            return null;
        }
        return new Account(obj.toMap());
    }

    public void remove(final Account document) {
        players.remove(new BasicDBObject("playername", document.getName()));
    }

    public void save(final Account document) {
        players.update(new BasicDBObject("playername", document.getName()), document, true, false);
    }

    public int countIp(final int ip) {
        return players.find(new BasicDBObject("ip", ip)).count();
    }

    private void ensureIndex() {
        players.ensureIndex(new BasicDBObject("playername", true));
        players.ensureIndex(new BasicDBObject("ip", true));
    }
}
