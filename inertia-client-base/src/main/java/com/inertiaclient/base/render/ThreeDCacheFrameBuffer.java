package com.inertiaclient.base.render;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.mixin.mixins.accessors.GameRendererAccessor;
import com.inertiaclient.base.render.skia.SkiaVulkanInstance;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.textures.AddressMode;
import com.mojang.renderpearl.api.textures.FilterMode;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.RenderPipelines;
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

    public void drawWithRenderer(PoseStack poseStack, float delta) {
        if (this.renderer != null) {
            if (this.shouldUpdate()) {
                RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(this.framebuffer.getColorTexture(), GuiRenderer.CLEAR_COLOR, this.framebuffer.getDepthTexture(), 0.0);

                this.renderer.render(poseStack, this.renderPassBuffer, delta);

                var oldFog = RenderSystem.getShaderFog();
                try (FeatureRenderDispatcher.PreparedFrame frame = InertiaBase.mc.gameRenderer.featureRenderDispatcher().prepareFrame(this.renderPassBuffer); RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "inertia_3d_renderpass11", this.framebuffer.getColorTextureView(), Optional.empty(), this.framebuffer.getDepthTextureView(), OptionalDouble.empty());) {
                    RenderSystem.setShaderFog(((GameRendererAccessor) InertiaBase.mc.gameRenderer).getFogRenderer().getBuffer(FogRenderer.FogMode.NONE));
                    RenderSystem.bindDefaultUniforms(renderPass);

                    FeatureRenderDispatcher.renderAllFeatures(renderPass, frame);
                }
                RenderSystem.setShaderFog(oldFog);
            }
        }
    }

    //flip y
    public void renderCachedImage(GuiGraphicsExtractor graphics) {
        graphics.pose().pushMatrix();
        graphics.pose().scale(1 / SkiaVulkanInstance.getScaleFactor(), 1 / SkiaVulkanInstance.getScaleFactor());
        graphics.innerBlit(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, this.framebuffer.getColorTextureView(), RenderSystem.getSamplerCache().getSampler(AddressMode.REPEAT, AddressMode.REPEAT, FilterMode.NEAREST, FilterMode.LINEAR, false), 0, 0, this.framebuffer.width, this.framebuffer.height, 0, 1, 1, 0, -1);
        graphics.pose().popMatrix();
    }

    public interface Render {

        void render(PoseStack poseStack, SubmitNodeStorage renderPassBuffer, float tickDelta);
    }


}
