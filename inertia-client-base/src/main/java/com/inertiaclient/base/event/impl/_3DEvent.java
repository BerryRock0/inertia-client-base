package com.inertiaclient.base.event.impl;

import com.inertiaclient.base.event.Event;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.renderer.SubmitNodeStorage;

@AllArgsConstructor
public class _3DEvent extends Event {

    @Getter
    private PoseStack poseStack;
    @Getter
    private SubmitNodeStorage renderPassBuffer;
    @Getter
    private float tickDelta;

}
