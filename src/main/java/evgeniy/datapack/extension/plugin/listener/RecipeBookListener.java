package evgeniy.datapack.extension.plugin.listener;

import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import evgeniy.datapack.extension.plugin.DataPackExtensionPlugin;
import evgeniy.datapack.extension.plugin.recipebook.RecipePlacer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.Recipe;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class RecipeBookListener implements Listener {

    private Plugin plugin;

    public RecipeBookListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(PlayerRecipeBookClickEvent event) {
        plugin.getLogger().info("Book Click");
        event.setCancelled(true);

        final var recipe = event.getRecipe();
        final var handle = ((CraftPlayer) event.getPlayer()).getHandle();

        new RecipePlacer<>((RecipeBookMenu<?>) handle.connection.player.containerMenu).tryPlaceRecipe(event.getPlayer(), recipe, event.isMakeAll());
//        final var recipeId = event.getRecipe();
//
//        final var player = event.getPlayer();
//        final var openInventory = player.getOpenInventory();
//
//        if (openInventory instanceof PlayerInventory inv) {
//            final IRecipe<?> recipe = getRecipe(recipeId);
//            if (recipe == null) return;
//
//
//        }
//        else if (openInventory instanceof CraftingInventory inv) {
//            final IRecipe<?> recipe = getRecipe(recipeId);
//            if (recipe == null) return;
//
//
//        }
    }

    @EventHandler
    public void on(CraftItemEvent event) {
        plugin.getLogger().info("Craft Item");
    }

    @Contract("null -> null")
    private @Nullable Recipe<?> getRecipe(@Nullable NamespacedKey id) {
        if (id == null) return null;

        final var recipeId = new ResourceLocation(id.namespace(), id.value());

        final var server = ((CraftServer) Bukkit.getServer()).getServer();
        final var craftingManager = server.getRecipeManager();

        return craftingManager.byKey(recipeId).orElse(null);
    }

}
