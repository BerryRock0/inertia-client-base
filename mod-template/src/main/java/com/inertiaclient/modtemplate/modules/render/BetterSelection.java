package com.inertiaclient.modtemplate.modules.render;

import com.inertiaclient.base.event.EventListener;
import com.inertiaclient.base.event.EventTarget;
import com.inertiaclient.base.event.impl._3DEvent;
import com.inertiaclient.base.module.Category;
import com.inertiaclient.base.module.Module;
import com.inertiaclient.base.utils.WorldRenderUtils;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.awt.Color;

public class BetterSelection extends Module {

    @EventTarget
    private final EventListener<_3DEvent> _3DListener = this::onEvent;

    public BetterSelection() {
        super("better_selection", Category.Render);
    }

    public void onEvent(_3DEvent event) {
        var hitResult = mc.hitResult;
        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            WorldRenderUtils.drawBoxESP(event.getPoseStack(), event.getRenderPassBuffer(), blockHitResult.getBlockPos(), new AABB(0, 0, 0, 1, 1, 1), true, true, Color.red);
        }

    }


}
