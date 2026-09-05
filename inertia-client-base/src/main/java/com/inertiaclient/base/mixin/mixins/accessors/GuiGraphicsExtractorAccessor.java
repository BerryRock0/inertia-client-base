package com.inertiaclient.base.mixin.mixins.accessors;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiGraphicsExtractor.class)
public interface GuiGraphicsExtractorAccessor {

    @Invoker("innerBlit")
    void callInnerBlit(final RenderPipeline pipeline, final GpuTextureView textureView, final GpuSampler sampler, final int x0, final int y0, final int x1, final int y1, final float u0, final float u1, final float v0, final float v1, final int color);
}
