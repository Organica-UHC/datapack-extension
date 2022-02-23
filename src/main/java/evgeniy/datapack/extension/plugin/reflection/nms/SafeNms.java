package evgeniy.datapack.extension.plugin.reflection.nms;

import evgeniy.datapack.extension.plugin.reflection.SafeClass;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class SafeNms {

    private static final String VERSION;
    private static final String BUKKIT_PACKAGE;
    private static final String MINECRAFT_PACKAGE;

    static {
        String finalVersion = null;
        try {
            finalVersion = Bukkit.getServer().getClass().toString().split("\\.")[3];
        } catch (IndexOutOfBoundsException ignore) { }
        VERSION = finalVersion;

        BUKKIT_PACKAGE = "org.bukkit.craftbukkit." + VERSION + ".";
        MINECRAFT_PACKAGE = "net.minecraft.";
    }

    public static SafeClass<?> getBukkitClass(@NotNull String name) {
        return SafeClass.forName(BUKKIT_PACKAGE + name);
    }

    public static SafeClass<?> getMinecraftClass(@NotNull String name) {
        return SafeClass.forName(MINECRAFT_PACKAGE + name);
    }

    @SuppressWarnings("unchecked")
    public static <T> SafeClass<T> getMinecraftClass(@NotNull String name, @NotNull Class<T> castType) {
        return (SafeClass<T>) SafeClass.forName(MINECRAFT_PACKAGE + name);
    }

}
