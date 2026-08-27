package com.inertiaclient.base.mixin.mixins;

import com.inertiaclient.base.event.EventManager;
import com.inertiaclient.base.event.impl._3DEvent;
import com.inertiaclient.base.render._2D3DRender;
import com.inertiaclient.base.render.animation.AnimationValue;
import com.inertiaclient.base.utils.opengl.CoordinateDimensionTranslator;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.commands.RenderPass;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4fStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.OptionalDouble;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Unique
    private long inertia$lastFrameTime;

    @Shadow
    @Final
    private GameRenderState gameRenderState;

    @Shadow
    @Final
    private RenderTarget mainRenderTarget;
    @Shadow
    @Final
    private FeatureRenderDispatcher featureRenderDispatcher;
    @Shadow
    @Final
    private FogRenderer fogRenderer;
    @Unique
    private SubmitNodeStorage inertia$3dPassStorage = new SubmitNodeStorage();

    @Inject(method = "extract", at = @At("HEAD"))
    public void extract(DeltaTracker deltaTracker, boolean advanceGameTime, CallbackInfo callbackInfo) {
        long currentTime = System.nanoTime();
        long delta = currentTime - inertia$lastFrameTime;
        inertia$lastFrameTime = currentTime;
        AnimationValue.tweenEngine.update(delta);
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;render3dHud(Lnet/minecraft/client/renderer/state/level/CameraRenderState;Lnet/minecraft/client/renderer/state/OptionsRenderState;Z)V"))
    private void renderWorld(CallbackInfo ci) {
        CameraRenderState cameraState = this.gameRenderState.levelRenderState.cameraRenderState;
        float worldPartialTicks = this.gameRenderState.levelRenderState.worldPartialTicks;

        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        modelViewStack.mul(cameraState.viewRotationMatrix);


        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        EventManager.fire(new _3DEvent(poseStack, this.inertia$3dPassStorage, worldPartialTicks));
        poseStack.popPose();
        {
            var oldFog = RenderSystem.getShaderFog();
            try (FeatureRenderDispatcher.PreparedFrame frame = this.featureRenderDispatcher.prepareFrame(this.inertia$3dPassStorage); RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "inertia_3d_renderpass", this.mainRenderTarget.getColorTextureView(), Optional.empty(), this.mainRenderTarget.getDepthTextureView(), OptionalDouble.empty());) {
                RenderSystem.setShaderFog(this.fogRenderer.getBuffer(FogRenderer.FogMode.NONE));
                RenderSystem.bindDefaultUniforms(renderPass);

                FeatureRenderDispatcher.renderAllFeatures(renderPass, frame);
            }
            RenderSystem.setShaderFog(oldFog);
        }

        CoordinateDimensionTranslator.setMatrixInformation(cameraState.viewRotationMatrix, cameraState.projectionMatrix);
        poseStack.pushPose();

        _2D3DRender.render(worldPartialTicks, null, false);

        poseStack.popPose();
        modelViewStack.popMatrix();
    }

}
