package evgeniy.datapack.extension.plugin;

import org.bukkit.plugin.java.JavaPlugin;

public class DataPackExtensionPlugin extends JavaPlugin {

    private final DataPackExtension instance = new DataPackExtension(this);

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
