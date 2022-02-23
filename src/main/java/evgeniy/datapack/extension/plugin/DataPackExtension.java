package evgeniy.datapack.extension.plugin;

import evgeniy.datapack.extension.plugin.injector.RecipeInjector;
import evgeniy.datapack.extension.plugin.listener.RecipeBookListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class DataPackExtension {

    private final Plugin plugin;

    public DataPackExtension(Plugin plugin) {
        this.plugin = plugin;
    }

    public void onLoad() {
//        final var protocolManager = ProtocolLibrary.getProtocolManager();
//        protocolManager.addPacketListener(new PacketListener() {
//            @Override
//            public void onPacketSending(final PacketEvent event) {
//                Bukkit.broadcast(Component.text("->" + event.getPacketType()));
//            }
//
//            @Override
//            public void onPacketReceiving(final PacketEvent event) {
//                Bukkit.broadcast(Component.text("<-" + event.getPacketType()));
//            }
//
//            @Override
//            public ListeningWhitelist getSendingWhitelist() {
//                return ListeningWhitelist.newBuilder()
//                        .gamePhase(GamePhase.PLAYING)
//                        .normal()
//                        .types(
//                                PacketType.Play.Client.RECIPE_DISPLAYED,
//                                PacketType.Play.Client.RECIPE_SETTINGS,
//                                PacketType.Play.Client.AUTO_RECIPE,
//                                PacketType.Play.Server.RECIPES,
//                                PacketType.Play.Server.RECIPE_UPDATE,
//                                PacketType.Play.Server.AUTO_RECIPE
//                        )
//                        .build();
//            }
//
//            @Override
//            public ListeningWhitelist getReceivingWhitelist() {
//                return ListeningWhitelist.newBuilder()
//                        .gamePhase(GamePhase.PLAYING)
//                        .normal()
//                        .types(
//                                PacketType.Play.Client.RECIPE_DISPLAYED,
//                                PacketType.Play.Client.RECIPE_SETTINGS,
//                                PacketType.Play.Client.AUTO_RECIPE,
//                                PacketType.Play.Server.RECIPES,
//                                PacketType.Play.Server.RECIPE_UPDATE,
//                                PacketType.Play.Server.AUTO_RECIPE
//                        )
//                        .build();
//            }
//
//            @Override
//            public Plugin getPlugin() {
//                return DataPackExtensionPlugin.this;
//            }
//        });
    }

    public void onEnable() {

        // load config

        new RecipeInjector().inject();

        Bukkit.getServer().reloadData();

        Bukkit.getPluginManager().registerEvents(new RecipeBookListener(plugin), plugin);
    }

    public void onDisable() {
        if (!plugin.getServer().isStopping()) {
            plugin.getLogger().severe("I dont like reloading!");

            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

}
