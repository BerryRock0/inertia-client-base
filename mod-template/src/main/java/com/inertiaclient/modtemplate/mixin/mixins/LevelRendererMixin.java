package com.inertiaclient.modtemplate.mixin.mixins;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.modtemplate.modules.render.BetterSelection;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "submitBlockOutline", at = @At("HEAD"), cancellable = true)
    public void submitBlockOutline(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, LevelRenderState levelRenderState, CallbackInfo ci) {
        //both these work
        //InertiaBase.instance.getModuleManager().getModule(BetterSelection.class);
        //ModTemplate.instance.getModules().getBetterSelection();
        if (InertiaBase.instance.getModuleManager().getModule(BetterSelection.class).isEnabled()) {
            ci.cancel();
        }
    }


}
