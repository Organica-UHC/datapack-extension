package evgeniy.datapack.extension.plugin.serializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import evgeniy.datapack.extension.plugin.mc.Items;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class ItemSerializer {

    public static ItemStack stackFromJson(JsonObject json) {
        var stack = itemFromJson(json);

        if (json.has("nbt")) {
            try {
                CompoundTag compoundTag = TagParser.parseTag(GsonHelper.convertToString(json.get("nbt"), "nbt"));
                stack.setTag(compoundTag);
            } catch (CommandSyntaxException var4) {
                throw new JsonSyntaxException("Invalid nbt tag: " + var4.getMessage());
            }
        }

//        final var tag = stack.getOrCreateTag();
//        tag.setString("customId", "datapack_extension:testing");

        int count = GsonHelper.getAsInt(json, "count", 1);
        if (count < 1) throw new JsonSyntaxException("Invalid output count: " + count);
        stack.setCount(count);

        return stack;
    }

    public static ItemStack itemFromJson(JsonObject json) {
        String itemIdName = GsonHelper.getAsString(json, "item");
        final var itemId = new ResourceLocation(itemIdName);

        final var optionalItem = Registry.ITEM.getOptional(itemId);
        if (optionalItem.isPresent()) {
            final var item = optionalItem.get();
            if (item == Items.AIR) throw new JsonSyntaxException("Invalid item: " + itemIdName);

            return new ItemStack(item);
        }

        final var optionalStack = Optional.<ItemStack>empty(); // get from custom registry
        if (optionalStack.isPresent()) {
            final var stack = optionalStack.get();
            if (stack.getItem() == Items.AIR) throw new JsonSyntaxException("Invalid item: " + itemIdName);

            return stack;
        }

        throw new JsonSyntaxException("Unknown item '" + itemId + "'");
    }

}
