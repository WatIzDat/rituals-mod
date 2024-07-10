package watizdat.rituals.state.component.abstraction;

import dev.onyxstudios.cca.api.v3.component.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class OptionalComponent<T> implements Component {
    @Nullable
    protected T value = null;

    @Nullable
    public T getValue() {
        return value;
    }

    public void set(@NotNull T value) {
        this.value = value;
    }

    public void remove() {
        value = null;
    }

    public boolean isPresent() {
        return value != null;
    }

    public boolean isEmpty() {
        return value == null;
    }
}
