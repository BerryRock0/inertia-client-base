package com.inertiaclient.modtemplate.modules.render;

import com.inertiaclient.base.event.EventListener;
import com.inertiaclient.base.event.EventTarget;
import com.inertiaclient.base.event.impl._3DCachedEvent;
import com.inertiaclient.base.module.Category;
import com.inertiaclient.base.module.Module;
import com.inertiaclient.base.utils.WorldRenderUtils;
import com.inertiaclient.base.value.impl.ColorValue;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.awt.Color;

public class BetterSelection extends Module {

    private ColorValue color;

    @EventTarget
    private final EventListener<_3DCachedEvent> _3DListener = this::onEvent;

    public BetterSelection() {
        super("better_selection", Category.Render);

        this.color = new ColorValue("color", this.getMainGroup(), Color.red);
    }

    public void onEvent(_3DCachedEvent event) {
        var hitResult = mc.hitResult;
        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;

            var esp = WorldRenderUtils.getFreshESPBuilder().fromBlockPos(blockHitResult.getBlockPos()).setColor(this.color.getValue().getRenderColor());
            esp.draw(event.getPoseStack(), event.getRenderPassBuffer());
        }

    }


}
