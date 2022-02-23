package evgeniy.datapack.extension.plugin.reflection;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class SafeMethod<T> {

    @Getter private final Method target;

    private SafeMethod(Method method) {
        this.target = method;
    }

    @SuppressWarnings("unchecked")
    public @Nullable T invoke(@Nullable Object instance, Object... arguments) {
        if (target == null) return null;
        try {
            target.setAccessible(true);
            return (T) target.invoke(instance, arguments);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        return null;
    }

    public static SafeMethod<Void> of(@Nullable Method method) {
        return of(method, void.class);
    }

    public static <T> SafeMethod<T> of(@Nullable Method method, @NotNull Class<T> resultType) {
        return new SafeMethod<>(method);
    }

}
