package evgeniy.datapack.extension.plugin.reflection.nms;

import evgeniy.datapack.extension.plugin.reflection.SafeClass;
import evgeniy.datapack.extension.plugin.reflection.SafeField;

import java.util.List;

public class ItemMetaNms {

    public static final SafeField<String> JSON_DISPLAY_NAME;
    public static final SafeField<List<String>> JSON_LORE;

    static {

        final SafeClass<?> craftMetaItemClass = SafeNms.getBukkitClass("inventory.CraftMetaItem");

        JSON_DISPLAY_NAME = craftMetaItemClass.getField("displayName", String.class);
        JSON_LORE = (SafeField<List<String>>) craftMetaItemClass.getField("lore");

    }

}
