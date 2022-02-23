package evgeniy.datapack.extension.plugin.recipebook;

import java.util.ArrayList;

public class LockedItemStackList extends ArrayList<LockedItemStack> {

    public void unlockAll() {
        forEach(LockedItemStack::unlock);
    }

    public int lockedCount() {
        return stream().mapToInt(LockedItemStack::getLockedCount).sum();
    }

}
