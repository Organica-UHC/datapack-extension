package evgeniy.datapack.extension.plugin.recipebook;

import lombok.experimental.ExtensionMethod;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.logging.Logger;

public class RecipePlacer<C extends Container> implements PlaceRecipe<RecipePlaceStrategy.Transaction> {

    public final Logger logger = Logger.getLogger(getClass().getName());

    private Inventory playerInventory;
    private final RecipeBookMenu<C> recipeBook;

    private final AvailablePlayerItems availableItems = new AvailablePlayerItems();

    public RecipePlacer(RecipeBookMenu<C> recipeBook) {
        this.recipeBook = recipeBook;
    }

    public void tryPlaceRecipe(@NotNull Player player, @Nullable NamespacedKey recipeId, boolean craftAll) {
        if (recipeId == null) {
            logger.info("recipeId are null");
            return;
        }

        final var recipe = getRecipe(recipeId);
        if (recipe == null) {
            logger.info("recipe are null");
            return;
        }

        final var handle = ((CraftPlayer) player).getHandle();
        final var recipeIsUnlocked = handle.getRecipeBook().contains(recipe);
        if (!recipeIsUnlocked) {
            logger.info("recipe is locked");
            return;
        }

        boolean canPlaceRecipe = handle.isCreative() || canClearGrid();
        if (!canPlaceRecipe) {
            logger.info("recipe cant place");
            return;
        }

        this.playerInventory = handle.getInventory();

        this.availableItems.clear();
        this.playerInventory.fillStackedContents(this.availableItems);
        this.recipeBook.fillCraftSlotsStackedContents(this.availableItems);

        boolean hasAllIngredients = this.availableItems.hasAllIngredients(recipe);
        if (hasAllIngredients) {
            Logger.getLogger(RecipePlacer.class.getName()).info(recipeId + " has all ingredients");
            placeRecipe(handle, recipe, craftAll);
        }
        else {
            clearGrid(false);
            handle.connection.send(new ClientboundPlaceGhostRecipePacket(handle.containerMenu.containerId, recipe));
        }
    }

    public void placeRecipe(ServerPlayer player, Recipe<? super C> recipe, boolean craftAll) {
        logger.info(recipe.getId().toString());
        boolean recipeMatches = this.recipeBook.recipeMatches(recipe);

        int beforeCount = 0;
        if (recipeMatches) {
            int count = Integer.MAX_VALUE;
            for(int slotIndex = 0; slotIndex < this.recipeBook.getGridHeight() * this.recipeBook.getGridWidth() + 1; ++slotIndex) {
                if (slotIndex == this.recipeBook.getResultSlotIndex()) continue;

                ItemStack stack = this.recipeBook.getSlot(slotIndex).getItem();
                if (stack.isEmpty()) continue;

                count = Integer.min(count, stack.getCount());
            }

            if (count == Integer.MAX_VALUE) throw new UnsupportedOperationException();

            beforeCount = count;
        }
        logger.info("RECIPE MATCHES = " + recipeMatches + " BEFORE COUNT " + beforeCount);

        final var placeStrategy = this.availableItems.makePlaceStrategy(recipe, beforeCount, craftAll);

        this.clearGrid(false);
        this.placeRecipe(
                this.recipeBook.getGridWidth(),
                this.recipeBook.getGridHeight(),
                this.recipeBook.getResultSlotIndex(),
                recipe,
                placeStrategy.transactions().iterator(),
                placeStrategy.maxStackSize()
        );

//        final var biggestCraftableStack = this.availableItems.getBiggestCraftableStack(recipe);
//
//        logger.info(String.valueOf(biggestCraftableStack));

//        int biggestCraftableStack = this.stackedContents.getBiggestCraftableStack(recipe, (IntList)null);
//        if (recipeMatches) {
//            for(int j = 0; j < this.recipeBook.getGridHeight() * this.recipeBook.getGridWidth() + 1; ++j) {
//                if (j != this.recipeBook.getResultSlotIndex()) {
//                    ItemStack itemstack = this.recipeBook.getSlot(j).getItem();
//                    if (!itemstack.isEmpty() && Math.min(biggestCraftableStack, itemstack.getMaxStackSize()) < itemstack.getCount() + 1) {
//                        return;
//                    }
//                }
//            }
//        }
//
//        int j1 = this.getStackSize(craftAll, biggestCraftableStack, recipeMatches);
//        IntList intlist = new IntArrayList();
//        if (this.stackedContents.canCraft(recipe, intlist, j1)) {
//            int k = j1;
//
//            for(int l : intlist) {
//                int i1 = StackedContents.fromStackingIndex(l).getMaxStackSize();
//                if (i1 < k) {
//                    k = i1;
//                }
//            }
//
//            if (this.stackedContents.canCraft(recipe, intlist, k)) {
//                this.clearGrid(false);
//                this.placeRecipe(this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), recipe, intlist.iterator(), k);
//            }
//        }
    }

    private void clearGrid(boolean bl) {
        for(int i = 0; i < this.recipeBook.getSize(); ++i) {
            if (this.recipeBook.shouldMoveToInventory(i)) {
                ItemStack itemStack = this.recipeBook.getSlot(i).getItem().copy();
                this.playerInventory.placeItemBackInInventory(itemStack, false);
                this.recipeBook.getSlot(i).set(itemStack);
            }
        }

        this.recipeBook.clearCraftingContent();
    }

    private boolean canClearGrid() {
        return false;
    }

    private int findSlotMatchingUnusedItem(ItemStack item) {
        for (int slot = 0; slot < this.playerInventory.items.size(); slot++) {
            final var inventoryItem = this.playerInventory.items.get(slot);
            if (inventoryItem.isEmpty()) continue;
            if (inventoryItem.isDamaged()) continue;
            if (inventoryItem.isEnchanted()) continue;
            if (inventoryItem.hasCustomHoverName()) continue;
            if (ItemStack.isSameItemSameTags(inventoryItem, item)) {
                return slot;
            }
        }

        return -1;
    }

    @Override
    public void addItemToSlot(Iterator<RecipePlaceStrategy.Transaction> inputs, int slotIndex, int amount, int gridX, int gridY) {
        final var transaction = inputs.next();

        logger.info("slot=%s, amount=%s, gridX=%s, gridY=%s, next=%s".formatted(
                slotIndex, amount, gridX, gridY, transaction
        ));

        if (transaction.isEmpty()) return;

        final var unlimitedStack = transaction.getStack();
        final var stack = unlimitedStack.take();

        if (stack.isEmpty()) return;

        Slot slot = this.recipeBook.getSlot(slotIndex);
        for(int i = 0; i < amount; ++i) {
            this.moveItemToGrid(slot, stack);
        }
    }

    protected void moveItemToGrid(Slot slot, ItemStack item) {
        int slotIndex = findSlotMatchingUnusedItem(item);
        if (slotIndex == -1) return;

        ItemStack stack = this.playerInventory.getItem(slotIndex).copy();
        if (stack.isEmpty()) return;

        if (stack.getCount() > 1) {
            this.playerInventory.removeItem(slotIndex, 1);
        } else {
            this.playerInventory.removeItemNoUpdate(slotIndex);
        }

        if (slot.getItem().isEmpty()) {
            stack.setCount(1);
            slot.set(stack);
        } else {
            slot.getItem().grow(1);
        }
    }

    @SuppressWarnings("unchecked")
    private @Nullable Recipe<? super C> getRecipe(@Nullable NamespacedKey id) {
        if (id == null) return null;

        final var recipeId = new ResourceLocation(id.namespace(), id.value());

        final var server = ((CraftServer) Bukkit.getServer()).getServer();
        final var craftingManager = server.getRecipeManager();

        return (Recipe<? super C>) craftingManager.byKey(recipeId).orElse(null);
    }

}
