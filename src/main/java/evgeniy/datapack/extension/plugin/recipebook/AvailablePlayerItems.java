package evgeniy.datapack.extension.plugin.recipebook;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AvailablePlayerItems extends StackedContents {

    public static final Logger logger = Logger.getLogger(AvailablePlayerItems.class.getName());
    private Map<ResourceLocation, GroupedItemStack> groupedItems = new HashMap<>();

    public static @NotNull Optional<ResourceLocation> getCustomId(ItemStack self) {
        final CompoundTag customId = self.getTagElement("customId");
        if (customId == null) return Optional.empty();

        final var namespace = customId.getString("namespace");
        final var path = customId.getString("path");
        return Optional.of(new ResourceLocation(namespace, path));
    }
    public static @NotNull ResourceLocation getCustomOrOriginalId(ItemStack self) {
        return getCustomId(self).orElseGet(() -> Registry.ITEM.getKey(self.getItem()));
    }

    public void addItem(final ItemStack stack, final int maxCount) {
        final var itemId = getCustomOrOriginalId(stack);
        final var grouped = groupedItems.computeIfAbsent(itemId, GroupedItemStack::new);

        grouped.add(new LimitedItemStack(stack, maxCount));
    }

    @Override
    public void accountSimpleStack(ItemStack stack) {
        addItem(stack, Integer.MAX_VALUE);
    }

    @Override
    public void accountStack(ItemStack stack) {
        addItem(stack, Integer.MAX_VALUE);
    }

    @Override
    public void accountStack(ItemStack stack, int maxCount) {
        addItem(stack, maxCount);
    }

    @Override
    public void clear() {
        this.groupedItems.clear();
    }

    public boolean hasAllIngredients(final Recipe<?> recipe) {
        final var ingredients = recipe.getIngredients();

        List<LockedItemStackList> allLockedItems = new ArrayList<>();
        nextIngredient: for (final Ingredient ingredient : ingredients) {
            if (ingredient == Ingredient.EMPTY) continue;

            logger.info("===ing");
            final var stacks = ingredient.getItems();
            for (final ItemStack stack : stacks) {
                final var lockedItems = lock(stack);
                if (!lockedItems.isEmpty()) {
                    allLockedItems.add(lockedItems);
                    continue nextIngredient;
                }
            }
            logger.info("cannot find ingredient: " + Arrays.toString(stacks));
            logger.info("in recipe: " + recipe);

            allLockedItems.stream()
                    .flatMap(LockedItemStackList::stream)
                    .forEach(LockedItemStack::unlock);
            return false;
        }

        allLockedItems.stream()
                .flatMap(LockedItemStackList::stream)
                .forEach(LockedItemStack::unlock);
        return true;
    }

    public LockedItemStackList lock(ItemStack stack) {
        final var itemId = getCustomOrOriginalId(stack);
        final var grouped = this.groupedItems.get(itemId);
        if (grouped == null) {
            logger.info("no item in inventory");
            return new LockedItemStackList();
        }

        return grouped.tryLock(stack);
    }

    public <C extends Container> int getBiggestCraftableStack(final Recipe<? super C> recipe) {
        final var ingredientStacks = recipe.getIngredients()
                .stream()
                .filter(stack -> !stack.isEmpty())
                .collect(Collectors.toList());

        int maxStackable = Integer.MAX_VALUE;
        for (final Ingredient recipeStack : ingredientStacks) {
            final var ingredients = recipeStack.getItems();
            int min = Integer.MIN_VALUE;
            for (final ItemStack ingredient : ingredients) {
                min = Integer.max(min, ingredient.getItem().getMaxStackSize());
            }
            maxStackable = Integer.min(maxStackable, min);
        }



        return maxStackable;
    }

    public <C extends Container> RecipePlaceStrategy makePlaceStrategy(final Recipe<? super C> recipe,
                                                                       final int beforeCount,
                                                                       final boolean craftAll) {
        final var emptyStrategy = new RecipePlaceStrategy(false, 0, Collections.emptyList());
        if (recipe == null) return emptyStrategy;

        final var recipeIngredients = recipe.getIngredients();
        if (recipeIngredients.isEmpty()) return emptyStrategy;

        var ingredientsCount = new HashMap<Ingredient, Integer>();
        for (final Ingredient recipeStack : recipeIngredients) {
            ingredientsCount.merge(recipeStack, 1, Integer::sum);
        }

        final var emptyCount = ingredientsCount.remove(Ingredient.EMPTY);
        logger.info("EMPTY COUNT: " + emptyCount);

        final var variants = new HashMap<Ingredient, VariantsHolder>();

        for (final Map.Entry<Ingredient, Integer> entry : ingredientsCount.entrySet()) {
            final var ingredient = entry.getKey();
            final var count = entry.getValue();

            logger.info("COUNT: " + count + " ING: " + ingredient);

            final var inventoryItems = Arrays.stream(ingredient.getItems())
                    .map(stack -> this.groupedItems.get(getCustomOrOriginalId(stack)))
                    .filter(Objects::nonNull)
                    .flatMap(GroupedItemStack::items)
                    .sorted(Comparator.comparingInt(UnlimitedStackedItem::getCount).reversed())
                    .collect(Collectors.toList());

            int inventoryCount = Math.min(inventoryItems.size(), count);

            final var variantsIndexes = RecipeIndexes.RECIPE_INDEXES[count - 1][inventoryCount - 1];
            final var sizes = RecipeIndexes.INDEXES_SIZE[count - 1][inventoryCount - 1];

            final var holder = new VariantsHolder(
                    inventoryItems, variantsIndexes.length
            );

            for (int i = 0; i < variantsIndexes.length; i++) {
                final var variant = variantsIndexes[i];
                final var size = sizes[i];

                final var variantSize = calcVariantSum(variant, size, inventoryItems);
                final var variantHolder = new VariantHolder(variant, size, variantSize);
                holder.variants.add(variantHolder);

                if (holder.maxSize < variantSize) {
                    holder.maxVariant = variantHolder;
                    holder.maxSize = variantSize;
                }
            }

            holder.variants.sort(Comparator.comparingInt(VariantHolder::getVariantSize).reversed());
            variants.put(ingredient, holder);
        }

        int maxStackSize = craftAll ? Integer.MAX_VALUE : beforeCount + 1;
        for (final Map.Entry<Ingredient, VariantsHolder> entry : variants.entrySet()) {
            final var holder = entry.getValue();
            if (maxStackSize > holder.maxSize) {
                maxStackSize = holder.maxSize;
            }
        }

        for (final Map.Entry<Ingredient, VariantsHolder> entry : variants.entrySet()) {
            final var holder = entry.getValue();
            if (holder.maxSize == maxStackSize) continue;

            for (final VariantHolder variant : holder.getVariants()) {
                if (variant.variantSize >= maxStackSize && variant.variantSize < holder.maxSize) {
                    holder.maxVariant = variant;
                    holder.maxSize = variant.variantSize;
                }
            }
        }

        final var transactions = new ArrayList<RecipePlaceStrategy.Transaction>();

        for (final Ingredient recipeStack : recipeIngredients) {
            if (recipeStack == Ingredient.EMPTY) {
                transactions.add(new RecipePlaceStrategy.Transaction());
            }
            else {
                transactions.add(new RecipePlaceStrategy.Transaction(
                    variants.get(recipeStack).nextItem(), maxStackSize
                ));
            }
        }

        return new RecipePlaceStrategy(true, maxStackSize, transactions);
    }

    private int calcVariantSum(final int[] variant, final int[] sizes, final List<UnlimitedStackedItem> stacks) {
        int min = Integer.MAX_VALUE;

        for (int i = 0; i < variant.length; i++) {
            final var index = variant[i];
            final var size = sizes[i];

            final var stack = stacks.get(index);
            final var stackSize = Integer.min(stack.getMaxStackSize(), stack.getCount() / size);

            min = Integer.min(stackSize, min);
        }

        return min;
    }

    @Getter @Setter
    public static class VariantsHolder {

        private final List<UnlimitedStackedItem> items;
        private final List<VariantHolder> variants;

        private VariantHolder maxVariant = null;
        private int maxSize = Integer.MIN_VALUE;

        private int cursor = 0;

        public VariantsHolder(final List<UnlimitedStackedItem> items, int size) {
            this.items = items;
            this.variants = new ArrayList<>(size);
        }

        public UnlimitedStackedItem nextItem() {
            if (maxVariant == null) return null;
            if (cursor >= maxVariant.variant.length) return null;
            final var index = maxVariant.variant[cursor++];
            return items.get(index);
        }

    }

    @Getter
    public static class VariantHolder {

        private final int[] variant;
        private final int[] size;
        private final int variantSize;

        public VariantHolder(final int[] variant, final int[] size, final int variantSize) {
            this.variant = variant;
            this.size = size;
            this.variantSize = variantSize;
        }

    }

}