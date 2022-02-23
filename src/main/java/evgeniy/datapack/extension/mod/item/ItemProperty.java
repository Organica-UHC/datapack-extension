package evgeniy.datapack.extension.mod.item;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

public record ItemProperty<T>(@NonNull String namespace,
                              @NonNull String id,
                              @NonNull Class<T> type) {

    public static final Pattern ID_PATTERN = Pattern.compile("^(?<namespace>[a-z0-9_.-]+:)?(?<id>[a-z0-9_.-]+)$");
    public static final String DEFAULT_NAMESPACE = "minecraft";

    public static <V> ItemProperty<V> of(@NotNull String fullyId, @NotNull Class<V> type) {
        return of(fullyId, DEFAULT_NAMESPACE, type);
    }

    public static <V> ItemProperty<V> of(@NotNull String fullyId, @NotNull String defaultNamespace, @NotNull Class<V> type) {
        final var matcher = ID_PATTERN.matcher(fullyId);
        if (!matcher.find()) {
            throw new UnsupportedOperationException(fullyId);
        }

        final var namespace = matcher.group("namespace");
        final var id = matcher.group("id");

        return new ItemProperty<>(
                namespace == null ? defaultNamespace : namespace,
                id,
                type
        );
    }

    @Override
    public @NonNull
    String namespace() {
        return namespace;
    }

    @Override
    public @NonNull
    String id() {
        return id;
    }

    @Override
    public @NonNull
    Class<T> type() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ItemProperty) obj;
        return Objects.equals(this.namespace, that.namespace) &&
                Objects.equals(this.id, that.id) &&
                Objects.equals(this.type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, id, type);
    }

    @Override
    public String toString() {
        return "ItemProperty[namespace=%s, id=%s, type=%s]".formatted(namespace, id, type);
    }

}
