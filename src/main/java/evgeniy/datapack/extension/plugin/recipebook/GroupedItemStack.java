package evgeniy.datapack.extension.plugin.recipebook;

import evgeniy.datapack.extension.plugin.utils.ItemStackUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class GroupedItemStack {

    private final ResourceLocation itemId;
    private final List<UnlimitedStackedItem> items = new ArrayList<>();

    public GroupedItemStack(final ResourceLocation itemId) {
        this.itemId = itemId;
    }

    public Stream<UnlimitedStackedItem> items() {
        return this.items.stream();
    }

    public void add(LimitedItemStack item) {
        for (final UnlimitedStackedItem stackedItem : this.items) {
            if (stackedItem.is(item.getStack())) {
                stackedItem.add(item);
                return;
            }
        }
        final var stackedItem = new UnlimitedStackedItem(item.getStack().copy());
        stackedItem.add(item);
        this.items.add(stackedItem);
    }

    public LockedItemStackList tryLock(ItemStack stack) {
        var result = new LockedItemStackList();
        int left = stack.getCount();

        System.out.println("items = " + this.items.size());
        for (final UnlimitedStackedItem item : this.items) {
            if (ItemStackUtils.isExtendedIgnoreDisplay(stack, item.getBasedOn())) {
                var lockedItems = item.lock(left);
                var lockedItemsCount = lockedItems.lockedCount();
                if (lockedItemsCount > 0) {
                    result.addAll(lockedItems);
                    left -= lockedItemsCount;
                    if (left < 1) {
                        return result;
                    }
                }
            }
        }

        if (left > 0) {
            System.out.println("left = " + left);
            result.unlockAll();
            return new LockedItemStackList();
        }

        return result;
    }

}
