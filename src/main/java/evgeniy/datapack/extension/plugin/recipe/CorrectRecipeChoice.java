package evgeniy.datapack.extension.plugin.recipe;

import evgeniy.datapack.extension.plugin.utils.ItemStackUtils;
import lombok.experimental.ExtensionMethod;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

public class CorrectRecipeChoice implements RecipeChoice {

    private List<net.minecraft.world.item.ItemStack> choices;

    public CorrectRecipeChoice(net.minecraft.world.item.ItemStack... choices) {
        this(Arrays.asList(choices));
    }

    public CorrectRecipeChoice(List<net.minecraft.world.item.ItemStack> choices) {
        this.choices = choices;
    }

    @Override
    public @NotNull ItemStack getItemStack() {
        return CraftItemStack.asCraftMirror(choices.get(0).copy());
    }

    @Override
    public @NotNull RecipeChoice clone() {
        try {
            CorrectRecipeChoice clone = (CorrectRecipeChoice) super.clone();
            clone.choices = new ArrayList<>(this.choices);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public boolean test(@NotNull final ItemStack stack) {
        return test(((CraftItemStack) stack).handle);
    }

    public boolean test(@NotNull net.minecraft.world.item.ItemStack stack) {
        if (this.choices.isEmpty()) {
            return stack.isEmpty();
        }

        BiPredicate<CompoundTag, CompoundTag> predicate = (originalTags, extendedTags) -> {
            if (originalTags.isEmpty()) {
                return extendedTags.get("customId") == null;
            }
            else if (extendedTags.isEmpty()) {
                return originalTags.size() == 1 && originalTags.get("display") != null;
            }
            else {
                return Objects.equals(originalTags.get("customId"), extendedTags.get("customId"));
            }
        };

        ItemStackUtils.TagsFilter tagsFilter = (originalTags, extendedTags, originalKey, originalValue) ->
                originalKey.equalsIgnoreCase("display");

        for (final net.minecraft.world.item.ItemStack choice : this.choices) {
            if (choice.getItem() != stack.getItem()) continue;
//            Logger.getLogger(getClass().getName()).info("item are equal");

            if (choice.getCount() > stack.getCount()) {
//                Logger.getLogger(getClass().getName()).info("count");
                continue;
            }

            if (ItemStackUtils.isExtended(choice, stack, predicate, tagsFilter)) {
                return true;
            }
        }

        return false;
    }

}
