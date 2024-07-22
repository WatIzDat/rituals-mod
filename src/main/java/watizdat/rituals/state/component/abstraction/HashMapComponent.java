package watizdat.rituals.state.component.abstraction;

import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;
import java.util.Map;

public abstract class HashMapComponent<K, V> implements MapComponent<K, V> {
    private final Map<K, V> map = new HashMap<>();

    @Override
    public Map<K, V> getMap() {
        return map;
    }

    @Override
    public void put(K key, V value) {
        map.put(key, value);
    }

    @Override
    public void remove(K key) {
        map.remove(key);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        tag.getKeys().forEach(key -> {
            Map.Entry<K, V> singleEntry = readSingleEntryFromNbt(tag, key);

            map.put(singleEntry.getKey(), singleEntry.getValue());
        });
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            writeSingleEntryToNbt(tag, entry);
        }
    }
}
