package evgeniy.datapack.extension.plugin.recipebook;

import lombok.Getter;

@Getter
public class LockedItemStack {

    private final LimitedItemStack limitedStack;
    private final int lockedCount;

    private boolean unlocked = false;

    public LockedItemStack(final LimitedItemStack limitedStack, final int lockedCount) {
        this.limitedStack = limitedStack;
        this.lockedCount = lockedCount;
    }

    public void unlock() {
        if (unlocked) throw new UnsupportedOperationException();
        this.limitedStack.unlock(this.lockedCount);
        this.unlocked = true;
    }

}
