package evgeniy.datapack.extension.plugin.recipe;

import com.google.gson.*;
import evgeniy.datapack.extension.plugin.reflection.SafeConstructor;
import evgeniy.datapack.extension.plugin.reflection.SafeField;
import evgeniy.datapack.extension.plugin.reflection.nms.SafeNms;
import evgeniy.datapack.extension.plugin.serializer.ItemSerializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MyIngredient {

    private static final SafeConstructor<Ingredient.Value> c_Ingredient_TagValue = SafeNms
            .getMinecraftClass("world.item.crafting.Ingredient$b", Ingredient.Value.class)
            .getConstructor(Tag.class);

    private static final SafeField<Ingredient.Value[]> f_Ingredient_values = SafeNms
            .getMinecraftClass("world.item.crafting.Ingredient")
            .getField("b", Ingredient.Value[].class);

    private static Ingredient fromValues(Stream<? extends Ingredient.Value> providers) {
        Ingredient ingredient = new Ingredient(providers);
        ingredient.exact = true;

        final var ingredient_values = f_Ingredient_values.get(ingredient);
        return ingredient_values == null || ingredient_values.length == 0 ? Ingredient.EMPTY : ingredient;
    }

    private static Ingredient.Value valueFromJson(JsonObject json) {
        if (json.has("item") && json.has("tag")) {
            throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
        }

        if (json.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }

        if (json.has("item")) {
            return new Ingredient.ItemValue(ItemSerializer.stackFromJson(json));
        }

        if (json.has("tag")) {
            ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(json, "tag"));
            TagKey<Item> tag = TagKey.create(Registry.ITEM_REGISTRY, id);

            return new TagValue(tag);
        }

        throw new JsonParseException("An ingredient entry needs either a tag or an item");
    }

    public static Ingredient fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) throw new JsonSyntaxException("Item cannot be null");

        if (json.isJsonObject()) {
            return fromValues(Stream.of(valueFromJson(json.getAsJsonObject())));
        }

        if (!json.isJsonArray()) throw new JsonSyntaxException("Expected item to be object or array of objects");

        JsonArray jsonarray = json.getAsJsonArray();
        if (jsonarray.size() == 0)
            throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");

        return fromValues(
                StreamSupport.stream(jsonarray.spliterator(), false)
                        .map((jsonElement) ->
                            valueFromJson(GsonHelper.convertToJsonObject(jsonElement, "item"))
                        )
        );
    }

    public static class TagValue implements Ingredient.Value {

        private final TagKey<Item> tag;

        public TagValue(final TagKey<Item> tag) {
            this.tag = tag;
        }

        @Override
        public Collection<ItemStack> getItems() {
            final var holders = Registry.ITEM.getTagOrEmpty(this.tag);
            return StreamSupport.stream(holders.spliterator(), false)
                    .map(ItemStack::new)
                    .collect(Collectors.toList());
        }

        @Override
        public JsonObject serialize() {
            JsonObject object = new JsonObject();
            object.addProperty("tag", this.tag.location().toString());
            return object;
        }

    }

}
