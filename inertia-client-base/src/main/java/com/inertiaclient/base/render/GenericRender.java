package com.inertiaclient.base.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface GenericRender {

    void render(GuiGraphicsExtractor graphics, float mouseX, float mouseY, float delta);
}
