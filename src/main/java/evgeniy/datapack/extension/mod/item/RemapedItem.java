package evgeniy.datapack.extension.mod.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

public abstract class RemapedItem extends Item {

    protected final ItemProperties properties = new ItemProperties();

    protected CreativeModeTab category;
    protected Enchantment.Rarity rarity;
    protected int maxStackSize;
    protected int maxDamage;
    protected boolean isFireResistant;
    protected Item craftingRemainingItem;
    protected FoodProperties foodProperties;

    public RemapedItem() {
        super(new Item.Properties());

        this.properties.set(ItemProperties.MAX_STACK_SIZE, 25);
    }

}
