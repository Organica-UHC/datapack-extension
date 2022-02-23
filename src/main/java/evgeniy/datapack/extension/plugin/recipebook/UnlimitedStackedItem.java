package evgeniy.datapack.extension.plugin.recipebook;

import lombok.Getter;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
public class UnlimitedStackedItem {

    private final ItemStack basedOn;
    private final List<LimitedItemStack> items = new ArrayList<>();

    public UnlimitedStackedItem(final ItemStack basedOn) {
        this.basedOn = basedOn;
    }

    public Stream<LimitedItemStack> items() {
        return items.stream();
    }

    public int getCount() {
        return items()
                .mapToInt(LimitedItemStack::getCount)
                .sum();
    }

    public int getMaxStackSize() {
        return this.basedOn.getMaxStackSize();
    }

    public boolean is(ItemStack stack) {
        return Objects.equals(this.basedOn.getItem(), stack.getItem()) && Objects.equals(this.basedOn.tag, stack.tag);
    }

    public void add(LimitedItemStack stack) {
        this.items.add(stack);
    }

    public LockedItemStackList lock(final int count) {
        var result = new LockedItemStackList();

        int left = count;
        for (final LimitedItemStack item : this.items) {
            int locked = item.lock(left);
            if (locked > 0) {
                left -= locked;
                result.add(new LockedItemStack(item, locked));
                if (left < 1) {
                    return result;
                }
            }
        }

        return result;
    }

    public ItemStack take() {
        for (final LimitedItemStack item : this.items) {
            if (item.getCount() > 0) {
                return item.getStack();
            }
        }
        return null;
    }

}
