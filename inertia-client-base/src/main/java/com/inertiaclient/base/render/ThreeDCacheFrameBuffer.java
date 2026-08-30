package com.inertiaclient.base.render;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.mixin.mixins.accessors.GameRendererAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.commands.RenderPass;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.fog.FogRenderer;

import java.util.Optional;
import java.util.OptionalDouble;

public class ThreeDCacheFrameBuffer extends CachedFrameBuffer {

    @Getter
    private SubmitNodeStorage renderPassBuffer = new SubmitNodeStorage();
    @Setter
    protected Render renderer;

    public ThreeDCacheFrameBuffer() {
    }

    public ThreeDCacheFrameBuffer(Render renderer) {
        this.renderer = renderer;
    }

    /**
     *
     * @param poseStack
     * @param delta
     * @return whether it was updated this call or not
     */
    public boolean drawWithRenderer(PoseStack poseStack, float delta) {
        if (this.renderer != null) {
            if (this.shouldUpdate()) {
                RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(this.framebuffer.getColorTexture(), GuiRenderer.CLEAR_COLOR, this.framebuffer.getDepthTexture(), 0.0);

                this.renderer.render(poseStack, this.renderPassBuffer, delta);

                var oldFog = RenderSystem.getShaderFog();
                try (FeatureRenderDispatcher.PreparedFrame frame = InertiaBase.mc.gameRenderer.featureRenderDispatcher().prepareFrame(this.renderPassBuffer); RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "inertia_3d_renderpass11", this.framebuffer.getColorTextureView(), Optional.empty(), this.framebuffer.getDepthTextureView(), OptionalDouble.empty());) {
                    RenderSystem.setShaderFog(((GameRendererAccessor) InertiaBase.mc.gameRenderer).getFogRenderer().getBuffer(FogRenderer.FogMode.NONE));
                    RenderSystem.bindDefaultUniforms(renderPass);

                    this.renderFeatures(renderPass, frame);
                }
                RenderSystem.setShaderFog(oldFog);
                return true;
            }
        }
        return false;
    }

    //flip y
    public void renderCachedImage(GuiGraphicsExtractor graphics) {
        CachedFrameBuffer.blitRenderTarget(graphics, this.framebuffer, true);
    }

    protected void renderFeatures(RenderPass renderPass, FeatureRenderDispatcher.PreparedFrame frame) {
        FeatureRenderDispatcher.renderAllFeatures(renderPass, frame);
    }

    public interface Render {

        void render(PoseStack poseStack, SubmitNodeStorage renderPassBuffer, float tickDelta);
    }


}
