package com.inertiaclient.base.event.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeStorage;


public class _3DCachedEvent extends _3DEvent {

    public _3DCachedEvent(PoseStack poseStack, SubmitNodeStorage renderPassBuffer, float tickDelta) {
        super(poseStack, renderPassBuffer, tickDelta);
    }
}
