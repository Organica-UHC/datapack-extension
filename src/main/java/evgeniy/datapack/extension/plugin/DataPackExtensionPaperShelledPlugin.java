package evgeniy.datapack.extension.plugin;

import cn.apisium.papershelled.plugin.PaperShelledPlugin;
import cn.apisium.papershelled.plugin.PaperShelledPluginDescription;
import cn.apisium.papershelled.plugin.PaperShelledPluginLoader;
import evgeniy.datapack.extension.plugin.injector.RecipeInjector;
import evgeniy.datapack.extension.plugin.listener.RecipeBookListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class DataPackExtensionPaperShelledPlugin extends PaperShelledPlugin {

    private final DataPackExtension instance = new DataPackExtension(this);

    public DataPackExtensionPaperShelledPlugin(@NotNull PaperShelledPluginLoader loader, @NotNull PaperShelledPluginDescription paperShelledDescription, @NotNull PluginDescriptionFile description, @NotNull File file) {
        super(loader, paperShelledDescription, description, file);
    }

    @Override
    public void onLoad() {
        instance.onLoad();
    }

    @Override
    public void onEnable() {
        instance.onEnable();
    }

    @Override
    public void onDisable() {
        instance.onDisable();
    }

}
