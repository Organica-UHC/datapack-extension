package evgeniy.datapack.extension.mod.utils;

import org.spongepowered.asm.mixin.injection.IInjectionPointContext;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.points.BeforeFieldAccess;
import org.spongepowered.asm.mixin.injection.points.BeforeInvoke;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointData;

@InjectionPoint.AtCode("FIELD_BEFORE")
public class FieldBeforeInjectionPoint extends BeforeFieldAccess {

    public FieldBeforeInjectionPoint(final InjectionPointData data) {
        super(data);
    }

    @Override
    public RestrictTargetLevel getTargetRestriction(final IInjectionPointContext context) {
        return RestrictTargetLevel.ALLOW_ALL;
    }

}
