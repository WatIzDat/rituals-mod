package watizdat.rituals.state.component.abstraction;

import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;

public abstract class ArrayListComponent<E> implements ListComponent<E> {
    private final List<E> value = new ArrayList<>();

    @Override
    public List<E> getValue() {
        return value;
    }

    @Override
    public void add(E e) {
        value.add(e);
    }

    @Override
    public void remove(E e) {
        value.remove(e);
    }

    @Override
    public void clear() {
        value.clear();
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        tag.getKeys().forEach(key -> value.add(readSingleElementFromNbt(tag, key)));
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        for (int i = 0; i < value.size(); i++) {
            writeSingleElementToNbt(tag, value.get(i), i);
        }
    }
}
