package evgeniy.datapack.extension.mod.utils;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.spongepowered.asm.mixin.injection.IInjectionPointContext;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.points.BeforeInvoke;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

@InjectionPoint.AtCode("INVOKE_AFTER")
public class InvokeAfterInjectionPoint extends BeforeInvoke {

    public InvokeAfterInjectionPoint(final InjectionPointData data) {
        super(data);
    }

    @Override
    public RestrictTargetLevel getTargetRestriction(IInjectionPointContext context) {
        return RestrictTargetLevel.ALLOW_ALL;
    }

    @Override
    public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes) {
        List<AbstractInsnNode> list = (nodes instanceof List) ? (List<AbstractInsnNode>) nodes : new ArrayList<AbstractInsnNode>(nodes);

        super.find(desc, insns, list);

        int shift = 1;
        for (ListIterator<AbstractInsnNode> iter = list.listIterator(); iter.hasNext();) {
            int sourceIndex = insns.indexOf(iter.next());
            int newIndex = sourceIndex + shift;
            if (newIndex >= 0 && newIndex < insns.size()) {
                iter.set(insns.get(newIndex));
            } else {
                // Shifted beyond the start or end of the insnlist, into the dark void
                iter.remove();

                // Decorate the injector with the info in case it fails
                int absShift = Math.abs(shift);
                char operator = absShift != shift ? '-' : '+';
                this.addMessage(
                        "@At.shift offset outside the target bounds: Index (index(%d) %s offset(%d) = %d) is outside the allowed range (0-%d)",
                        sourceIndex, operator, absShift, newIndex, insns.size());
            }
        }

        if (nodes != list) {
            nodes.clear();
            nodes.addAll(list);
        }

        return nodes.size() > 0;
    }

}
