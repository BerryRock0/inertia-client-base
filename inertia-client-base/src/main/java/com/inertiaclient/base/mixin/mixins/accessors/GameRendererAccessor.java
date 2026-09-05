package com.inertiaclient.base.mixin.mixins.accessors;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

    @Accessor("fogRenderer")
    FogRenderer getFogRenderer();

    @Accessor("useUiLightmap")
    boolean getUseUiLightmap();

    @Accessor("useUiLightmap")
    void setUseUiLightmap(boolean useUiLightmap);
}
