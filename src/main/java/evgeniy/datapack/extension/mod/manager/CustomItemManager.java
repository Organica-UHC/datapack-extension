package evgeniy.datapack.extension.mod.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class CustomItemManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private static final String TYPE = "custom_items";

    public CustomItemManager() {
        super(GSON, TYPE);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> prepared, ResourceManager manager, ProfilerFiller profiler) {
        for (final Map.Entry<ResourceLocation, JsonElement> entry : prepared.entrySet()) {
            final var key = entry.getKey();
            final var value = entry.getValue();

            System.out.println(key);
            System.out.println(value);
            System.out.println();
        }
    }

}
