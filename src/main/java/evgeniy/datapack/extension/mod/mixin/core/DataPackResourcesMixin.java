package evgeniy.datapack.extension.mod.mixin.core;

import evgeniy.datapack.extension.mod.manager.CustomItemManager;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//@Mixin(targets = "net.minecraft.server.ServerResources")
@Mixin(targets = "net.minecraft.server.DataPackResources")
public class DataPackResourcesMixin {

//    @Shadow @Final private ReloadableResourceManager resources;
    @Shadow @Final private ReloadableResourceManager b;

    private final CustomItemManager itemManager = new CustomItemManager();


    @Inject(
//            method = "<init>(Lnet/minecraft/core/IRegistryCustom;Lnet/minecraft/commands/CommandDispatcher$ServerType;I)V",
            method = "<init>",
            at = @At(
                    value = "evgeniy.datapack.extension.mod.mixin.core.ConstructorBeforeInvoke",
                    target = "Lnet/minecraft/server/packs/resources/IReloadableResourceManager;a(Lnet/minecraft/server/packs/resources/IReloadListener;)V",
//                    target = "Lnet/minecraft/server/packs/resources/ReloadableResourceManager;registerReloadListener(Lnet/minecraft/server/packs/resources/PreparableReloadListener;)V",
                    ordinal = 0
            )
    )
    public void onConstruction(final CallbackInfo ci) {
//        this.resources.registerReloadListener(this.itemManager);
        this.b.registerReloadListener(this.itemManager);
    }

}
