package evgeniy.datapack.extension.plugin.reflection;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SafeClass<T> {

    @Getter private final Class<T> target;

    private SafeClass(Class<T> target) {
        this.target = target;
    }

    public @NotNull SafeConstructor<T> getConstructor(Class<?>... parameterTypes) {
        if (target == null) return SafeConstructor.of(null);
        try {
            return SafeConstructor.of(target.getDeclaredConstructor(parameterTypes));
        } catch (Exception e) {
            return SafeConstructor.of(null);
        }
    }

    public @NotNull SafeMethod<Void> getMethod(@NotNull String name, Class<?>... parameterTypes) {
        return getMethod(name, Void.class, parameterTypes);
    }

    public @NotNull <R> SafeMethod<R> getMethod(@NotNull String name, @NotNull Class<R> resultType, Class<?>... parameterTypes) {
        if (target == null) return SafeMethod.of(null, resultType);
        try {
            return SafeMethod.of(target.getDeclaredMethod(name, parameterTypes), resultType);
        } catch (Exception e) {
            return SafeMethod.of(null, resultType);
        }
    }

    public @NotNull SafeField<?> getField(@NotNull String name) {
        return getField(name, Object.class);
    }

    public @NotNull <R> SafeField<R> getField(@NotNull String name, @NotNull Class<R> resultType) {
        if (target == null) return SafeField.of(null, resultType);
        try {
            return SafeField.of(target.getDeclaredField(name), resultType);
        } catch (Exception e) {
            return SafeField.of(null, resultType);
        }
    }

    public static <T> SafeClass<T> of(@Nullable Class<T> target) {
        return new SafeClass<>(target);
    }

    public static SafeClass<?> forName(@NotNull String className) {
        try {
            return new SafeClass<>(Class.forName(className));
        } catch (ClassNotFoundException e) {
            return new SafeClass<>(null);
        }
    }

}
