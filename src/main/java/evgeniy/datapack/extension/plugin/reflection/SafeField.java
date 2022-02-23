package evgeniy.datapack.extension.plugin.reflection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class SafeField<T> {

    private final @Nullable Field target;

    private SafeField(@Nullable Field target) {
        this.target = target;
    }

    @SuppressWarnings("unchecked")
    public @Nullable T get(@Nullable Object instance) {
        if (target == null) return null;
        try {
            target.setAccessible(true);
            return (T) target.get(instance);
        } catch (Exception ignore) {
        }
        return null;
    }

    public void set(@Nullable Object instance, @Nullable T value) {
        if (target == null) return;
        try {
            target.setAccessible(true);
            target.set(instance, value);
        } catch (Exception ignore) {
        }
    }

    public static SafeField<Object> of(@Nullable Field field) {
        return of(field, Object.class);
    }

    public static <T> SafeField<T> of(@Nullable Field field, @NotNull Class<T> resultType) {
        return new SafeField<>(field);
    }

}
