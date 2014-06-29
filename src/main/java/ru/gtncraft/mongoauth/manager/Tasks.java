package ru.gtncraft.mongoauth.manager;

import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Tasks extends AbstractMap<UUID, Collection<Runnable>> {

    final Map<UUID, Collection<Runnable>> values;

    Tasks() {
        values = new ConcurrentHashMap<>();
    }

    public void schedule(final Player player, final Runnable task) {
        UUID uuid = player.getUniqueId();

        if (!containsKey(uuid)) {
            values.put(uuid, new ArrayList<>());
        }

        get(uuid).add(task);
    }

    public void execute(final Player player) {
        if (containsKey(player.getUniqueId())) {
            Iterator<Runnable> it = get(player.getUniqueId()).iterator();
            while (it.hasNext()) {
                Runnable task = it.next();
                task.run();
                it.remove();
            }
        }
    }

    @Override
    public Set<Entry<UUID, Collection<Runnable>>> entrySet() {
        return values.entrySet();
    }
}

