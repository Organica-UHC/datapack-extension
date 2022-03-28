package evgeniy.datapack.extension.mod.mixin.core;

import evgeniy.datapack.extension.mod.manager.CustomItemManager;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.ServerFunctionLibrary;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.tags.TagManager;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.storage.loot.ItemModifierManager;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.PredicateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ReloadableServerResources.class)
public abstract class ReloadableServerResourcesMixin {

    @Shadow @Final private TagManager tagManager;
    @Shadow @Final private PredicateManager predicateManager;
    @Shadow @Final private RecipeManager recipes;
    @Shadow @Final private LootTables lootTables;
    @Shadow @Final private ItemModifierManager itemModifierManager;
    @Shadow @Final private ServerFunctionLibrary functionLibrary;
    @Shadow @Final private ServerAdvancementManager advancements;

    private final CustomItemManager itemManager = new CustomItemManager();

    /**
     * @author Evgeniy
     */
    @Overwrite
    public List<PreparableReloadListener> listeners() {
        return List.of(
                this.itemManager,
                this.tagManager,
                this.predicateManager,
                this.recipes,
                this.lootTables,
                this.itemModifierManager,
                this.functionLibrary,
                this.advancements
        );
    }

}
