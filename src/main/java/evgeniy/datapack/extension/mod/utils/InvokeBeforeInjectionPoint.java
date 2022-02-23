package evgeniy.datapack.extension.mod.utils;

import org.spongepowered.asm.mixin.injection.IInjectionPointContext;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.points.BeforeInvoke;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointData;

@InjectionPoint.AtCode("INVOKE_BEFORE")
public class InvokeBeforeInjectionPoint extends BeforeInvoke {

    public InvokeBeforeInjectionPoint(final InjectionPointData data) {
        super(data);
    }

    @Override
    public RestrictTargetLevel getTargetRestriction(final IInjectionPointContext context) {
        return RestrictTargetLevel.ALLOW_ALL;
    }

}
