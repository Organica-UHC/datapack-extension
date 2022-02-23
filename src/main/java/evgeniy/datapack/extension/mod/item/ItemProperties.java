package evgeniy.datapack.extension.mod.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ItemProperties {

    public static final ItemProperty<Integer> MAX_STACK_SIZE = ItemProperty.of("max_stack_size", Integer.class);

    public static final ItemProperty<Integer> MAX_DURABILITY = ItemProperty.of("max_durability", Integer.class);

    public static final ItemProperty<CreativeModeTab> CREATIVE_CATEGORY = ItemProperty.of("category", CreativeModeTab.class);

    public static final ItemProperty<Enchantment.Rarity> RARITY = ItemProperty.of("rarity", Enchantment.Rarity.class);

    public static final ItemProperty<?> CRAFT_REMINDER_ITEM = ItemProperty.of("craft_reminder_item", Void.class);

    protected final Map<ItemProperty<?>, Object> datas = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> @Nullable T set(@NotNull ItemProperty<T> key, @Nullable T value) {
        if (value == null) {
            return this.remove(key);
        }
        else {
            return (T) this.datas.put(key, value);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> @Nullable T remove(@NotNull ItemProperty<T> key) {
        return (T) this.datas.remove(key);
    }

    @SuppressWarnings("unchecked")
    public <T> @NotNull T get(@NotNull ItemProperty<T> key) {
        return Objects.requireNonNull((T) this.datas.get(key));
    }

    @SuppressWarnings("unchecked")
    public <T> @NotNull Optional<T> tryGet(@NotNull ItemProperty<T> key) {
        return Optional.ofNullable((T) this.datas.get(key));
    }

    public @NotNull Stream<@NotNull ItemProperty<?>> keys() {
        return this.datas.keySet().stream();
    }

    public @NotNull Stream<@NotNull Object> values() {
        return this.datas.values().stream();
    }

    public @NotNull Stream<Map.Entry<@NotNull ItemProperty<?>, @NotNull Object>> entries() {
        return this.datas.entrySet().stream();
    }

}
