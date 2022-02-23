package evgeniy.datapack.extension.plugin.utils;

import lombok.experimental.ExtensionMethod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.logging.Logger;

public class ItemStackUtils {

    private static Logger logger = Logger.getLogger(ItemStackUtils.class.getName());

    public static boolean isExtended(ItemStack original, ItemStack extended) {
        return isExtended(
                original, extended,
                (originalTags, extendedTags) -> true,
                (originalTags, extendedTags, originalKey, originalValue) -> false
        );
    }

    public static boolean isNullOrEmpty(CompoundTag nbt) {
        return nbt == null || nbt.tags == null || nbt.isEmpty();
    }

    public static boolean isExtendedIgnoreDisplay(ItemStack original, ItemStack extended) {
        return isExtended(
                original, extended,
                (originalTags, extendedTags) -> {
                    if (isNullOrEmpty(originalTags)) {
                        return true;
                    }
                    else if (isNullOrEmpty(extendedTags)) {
                        return originalTags.size() == 1 && originalTags.get("display") != null;
                    }
                    else {
                        return true;
                    }
                },
                (originalTags, extendedTags, originalKey, originalValue) ->
                        originalKey.equalsIgnoreCase("display")
        );
    }

    public static boolean isExtended(ItemStack origin, ItemStack extended, BiPredicate<CompoundTag, CompoundTag> predicate, TagsFilter tagsFilter) {
        final var originTags = origin.tag;
        final var extendedTags = extended.tag;

        if (isNullOrEmpty(originTags) && isNullOrEmpty(extendedTags)) {
//            logger.info("1");
            return true;
        }
        else if (originTags != null && isNullOrEmpty(extendedTags)) {
//            logger.info("2");
            return predicate.test(originTags, extendedTags);
        }
        else if (isNullOrEmpty(originTags)) {
//            logger.info("3");
            return predicate.test(originTags, extendedTags);
        }
        else {
            if (!predicate.test(originTags, extendedTags)) {
//                logger.info("4");
                return false;
            }

            for (final Map.Entry<String, Tag> entry : originTags.tags.entrySet()) {
                final var originalNbtKey = entry.getKey();
                final var originalNbtValue = entry.getValue();

                if (tagsFilter.test(originTags, extendedTags, originalNbtKey, originalNbtValue)) {
                    continue;
                }

                final var extendedNbtValue = extendedTags.tags.get(originalNbtKey);
                if (extendedNbtValue == null) {
//                    logger.info("5");
                    return false;
                }

                if (!Objects.equals(originalNbtValue, extendedNbtValue)) {
//                    logger.info("6");
                    return false;
                }
            }
        }

//        logger.info("7");
        return true;
    }

    public interface TagsFilter {

        boolean test(@NotNull CompoundTag originalTags, @NotNull CompoundTag extendedTags,
                     @NotNull String originalKey, @NotNull Tag originalValue);

    }

}
