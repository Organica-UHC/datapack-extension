package evgeniy.datapack.extension.plugin.reflection;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;

public class SafeConstructor<T> {

    private Constructor<T> target;

    private SafeConstructor(Constructor<T> target) {
        this.target = target;
    }

    public @Nullable T newInstance(Object... initargs) {
        try {
            this.target.setAccessible(true);
            return this.target.newInstance(initargs);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> SafeConstructor<T> of(@Nullable Constructor<T> target) {
        return new SafeConstructor<>(target);
    }

}
