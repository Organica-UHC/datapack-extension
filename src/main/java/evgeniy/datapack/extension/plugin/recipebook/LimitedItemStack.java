package evgeniy.datapack.extension.plugin.recipebook;

import lombok.Getter;
import net.minecraft.world.item.ItemStack;

@Getter
public class LimitedItemStack {

    private final ItemStack stack;
    private final int max;

    private int locked = 0;

    public LimitedItemStack(final ItemStack stack, final int max) {
        this.stack = stack;
        this.max = max;
    }

    public int getCount() {
        return Math.min(stack.getCount(), max);
    }

    public int getAvailableCount() {
        return getCount() - locked;
    }

    public int lock(int count) {
        int toLock = Math.min(getAvailableCount(), count);
        this.locked += toLock;
        return toLock;
    }

    public void unlock(int count) {
        this.locked -= count;
        if (this.locked < 0) this.locked = 0;
    }

}
