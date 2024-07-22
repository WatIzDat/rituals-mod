package watizdat.rituals.state.component.abstraction;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.NbtCompound;

import java.util.Map;

public interface MapComponent<K, V> extends Component {
    Map<K, V> getMap();
    void put(K key, V value);
    void remove(K key);
    void clear();
    Map.Entry<K, V> readSingleEntryFromNbt(NbtCompound tag, String keyOfEntry);
    void writeSingleEntryToNbt(NbtCompound tag, Map.Entry<K, V> entry);
}
