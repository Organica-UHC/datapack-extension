package evgeniy.datapack.extension.plugin.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ShapedRecipeProxy extends ShapedRecipe {

    private final RecipeSerializer<ShapedRecipe> serializer;

    public ShapedRecipeProxy(final ResourceLocation id,
                              final String group,
                              final int width, final int height,
                              final NonNullList<Ingredient> ingredients,
                              final ItemStack output,
                              final RecipeSerializer<ShapedRecipe> serializer) {
        super(id, group, width, height, ingredients, output);
        this.serializer = serializer;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return this.serializer;
    }

    @Override
    public boolean matches(CraftingContainer inventory, Level world) {
        var containerWidth = inventory.getWidth();
        var containerHeight = inventory.getHeight();

        for(int dx = 0; dx <= containerWidth - this.getWidth(); ++dx) {
            for(int dy = 0; dy <= containerHeight - this.getHeight(); ++dy) {
                if (this.matches(inventory, dx, dy, true)) {
                    return true;
                }

                if (this.matches(inventory, dx, dy, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matches(@NotNull CraftingContainer inventory, int dx, int dy, boolean p_44174_) {
        var containerWidth = inventory.getWidth();
        var containerHeight = inventory.getHeight();

        for(int i = 0; i < containerWidth; ++i) {
            for(int j = 0; j < containerHeight; ++j) {
                int k = i - dx;
                int l = j - dy;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < this.getWidth() && l < this.getHeight()) {
                    if (p_44174_) {
                        ingredient = this.getIngredients().get(this.getWidth() - k - 1 + l * this.getWidth());
                    } else {
                        ingredient = this.getIngredients().get(k + l * this.getWidth());
                    }
                }

                final var items = ingredient.getItems();
                final var choice = new CorrectRecipeChoice(items);

                final var containerItem = inventory.getItem(i + j * containerWidth);
                if (!choice.test(containerItem)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public org.bukkit.inventory.ShapedRecipe toBukkitRecipe() {
        CraftShapedRecipe recipe;
        CraftItemStack result = CraftItemStack.asCraftMirror(this.getResultItem());
        recipe = new CraftShapedRecipe(result, this);
        recipe.setGroup(this.getGroup());

        heightSwitch: switch(getHeight()) {
            case 1:
                switch(getWidth()) {
                    case 1:
                        recipe.shape("a");
                        break heightSwitch;
                    case 2:
                        recipe.shape("ab");
                        break heightSwitch;
                    case 3:
                        recipe.shape("abc");
                    default:
                        break heightSwitch;
                }
            case 2:
                switch(getWidth()) {
                    case 1:
                        recipe.shape("a", "b");
                        break heightSwitch;
                    case 2:
                        recipe.shape("ab", "cd");
                        break heightSwitch;
                    case 3:
                        recipe.shape("abc", "def");
                    default:
                        break heightSwitch;
                }
            case 3:
                switch(getWidth()) {
                    case 1:
                        recipe.shape("a", "b", "c");
                        break;
                    case 2:
                        recipe.shape("ab", "cd", "ef");
                        break;
                    case 3:
                        recipe.shape("abc", "def", "ghi");
                }
        }

        char c = 'a';
        for (final Ingredient ingredient : getIngredients()) {
            recipe.setIngredient(c, new CorrectRecipeChoice(Arrays.asList(ingredient.getItems())));
            c++;
        }

        return recipe;
    }

}
