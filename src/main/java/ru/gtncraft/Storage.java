package ru.gtncraft;

import com.mongodb.*;
import org.bukkit.configuration.ConfigurationSection;

public class Storage {

    private DBCollection players;

	public Storage(ConfigurationSection config) throws Exception {
        MongoClient mongoClient = new MongoClient(config.getString("host"), config.getInt("port"));
        DB db = mongoClient.getDB(config.getString("name"));
        players = db.getCollection(config.getString("collection"));
        if (players.count() < 1) {
            ensureIndex();
        }
	}

    public Account get(String playername) {
        DBObject obj = players.findOne(new BasicDBObject("playername", playername.toLowerCase()));
        if (obj != null) {
            return new Account(obj.toMap());
        }
        return null;
    }

    public void remove(Account account) {
        players.remove(new BasicDBObject("playername", account.getName()));
    }

    public void save(Account account) {
        players.findAndModify(new BasicDBObject("playername", account.getName()), null, null, false, account, false, true);
    }

    public int countIp(int ip) {
        return players.find(new BasicDBObject("ip", ip)).count();
    }

    private void ensureIndex() {
        players.ensureIndex(new BasicDBObject("playername", true));
        players.ensureIndex(new BasicDBObject("ip", true));
    }
}
