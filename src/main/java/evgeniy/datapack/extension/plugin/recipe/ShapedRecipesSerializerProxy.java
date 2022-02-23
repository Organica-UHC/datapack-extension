package evgeniy.datapack.extension.plugin.recipe;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import evgeniy.datapack.extension.plugin.serializer.ItemSerializer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.Map;
import java.util.Set;

public class ShapedRecipesSerializerProxy extends ShapedRecipe.Serializer {

    @Override
    public ShapedRecipe fromJson(ResourceLocation id, JsonObject json) {
        return deserializeRecipe(id, json);
    }

    public ShapedRecipe deserializeRecipe(ResourceLocation id, JsonObject json) {
        String group = GsonHelper.getAsString(json, "group", "");

        Map<String, Ingredient> ingredients = ingredientsFromJson(GsonHelper.getAsJsonObject(json, "key"));

        String[] pattern = shrink(patternFromJson(GsonHelper.getAsJsonArray(json, "pattern")));

        int width = pattern[0].length();
        int height = pattern.length;

        NonNullList<Ingredient> input = dissolvePattern(pattern, ingredients, width, height);
        ItemStack output = ItemSerializer.stackFromJson(GsonHelper.getAsJsonObject(json, "result"));

        return new ShapedRecipeProxy(id, group, width, height, input, output, this);
    }

    public static Map<String, Ingredient> ingredientsFromJson(JsonObject json) {
        Map<String, Ingredient> result = Maps.newHashMap();

        for (final Map.Entry<String, JsonElement> entry : json.entrySet()) {
            final var key = entry.getKey();

            if (key.length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + key + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(key)) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            final var recipe = MyIngredient.fromJson(entry.getValue());
            result.put(key, recipe);
        }

        result.put(" ", Ingredient.EMPTY);
        return result;
    }

    public static String[] patternFromJson(JsonArray json) {
        String[] pattern = new String[json.size()];

        if (pattern.length > 3) throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        if (pattern.length == 0) throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");

        for (int i = 0; i < pattern.length; ++i) {
            String s = GsonHelper.convertToString(json.get(i), "pattern[" + i + "]");

            if (s.length() > 3) {
                throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
            }

            if (i > 0 && pattern[0].length() != s.length()) {
                throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
            }

            pattern[i] = s;
        }

        return pattern;
    }

    public static String[] shrink(String... pattern) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for (int i1 = 0; i1 < pattern.length; ++i1) {
            String s = pattern[i1];
            i = Math.min(i, firstNonSpace(s));
            int j1 = lastNonSpace(s);
            j = Math.max(j, j1);
            if (j1 < 0) {
                if (k == i1) {
                    ++k;
                }

                ++l;
            } else {
                l = 0;
            }
        }

        if (pattern.length == l) return new String[0];

        String[] resultPattern = new String[pattern.length - l - k];

        for (int k1 = 0; k1 < resultPattern.length; ++k1) {
            resultPattern[k1] = pattern[k1 + k].substring(i, j + 1);
        }

        return resultPattern;
    }

    public static int firstNonSpace(String str) {
        int index = 0;
        while (index < str.length() && str.charAt(index) == ' ') ++index;
        return index;
    }

    public static int lastNonSpace(String str) {
        int i = str.length() - 1;
        while (i >= 0 && str.charAt(i) == ' ') --i;
        return i;
    }

    public static NonNullList<Ingredient> dissolvePattern(String[] pattern, Map<String, Ingredient> ingredients, int width, int height) {
        NonNullList<Ingredient> result = NonNullList.withSize(width * height, Ingredient.EMPTY);

        Set<String> keys = Sets.newHashSet(ingredients.keySet());
        keys.remove(" ");

        for(int i = 0; i < pattern.length; ++i) {
            for(int j = 0; j < pattern[i].length(); ++j) {
                String key = pattern[i].substring(j, j + 1);
                Ingredient ingredient = ingredients.get(key);

                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + key + "' but it's not defined in the key");
                }

                keys.remove(key);
                result.set(j + width * i, ingredient);
            }
        }

        if (!keys.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + keys);
        }

        return result;
    }

}
