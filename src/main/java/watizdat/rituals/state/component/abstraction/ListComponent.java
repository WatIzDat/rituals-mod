package watizdat.rituals.state.component.abstraction;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

public interface ListComponent<E> extends Component {
    List<E> getValue();
    void add(E e);
    void remove(E e);
    void clear();
    E readSingleElementFromNbt(NbtCompound tag, String keyOfElement);
    void writeSingleElementToNbt(NbtCompound tag, E e, int indexOfElement);
}
