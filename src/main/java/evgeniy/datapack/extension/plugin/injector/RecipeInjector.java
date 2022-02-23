package evgeniy.datapack.extension.plugin.injector;

import evgeniy.datapack.extension.plugin.recipe.ShapedRecipesSerializerProxy;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class RecipeInjector implements Injector {

    public static final ShapedRecipesSerializerProxy SHAPED_RECIPES_SERIALIZER = new ShapedRecipesSerializerProxy();

    private boolean injected;

    @Override
    public boolean hasInjected() {
        return this.injected;
    }

    @Override
    public void inject() {
        if (this.injected) throw new UnsupportedOperationException();
        this.injected = true;

        Registry.register(Registry.RECIPE_SERIALIZER, "crafting_shaped", SHAPED_RECIPES_SERIALIZER);
    }

}
