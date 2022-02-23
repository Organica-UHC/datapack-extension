package evgeniy.datapack.extension.mod;

import evgeniy.datapack.extension.mod.utils.FieldAfterInjectionPoint;
import evgeniy.datapack.extension.mod.utils.FieldBeforeInjectionPoint;
import evgeniy.datapack.extension.mod.utils.InvokeAfterInjectionPoint;
import evgeniy.datapack.extension.mod.utils.InvokeBeforeInjectionPoint;
import org.spongepowered.asm.mixin.injection.InjectionPoint;

public class DataPackExtensionMod {

    public DataPackExtensionMod() {
        InjectionPoint.register(FieldBeforeInjectionPoint.class, null);
        InjectionPoint.register(FieldAfterInjectionPoint.class, null);
        InjectionPoint.register(InvokeBeforeInjectionPoint.class, null);
        InjectionPoint.register(InvokeAfterInjectionPoint.class, null);
    }

}
