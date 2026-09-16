package com.inertiaclient.base.gui.components.leftpanel;

import com.inertiaclient.base.InertiaBase;
import com.inertiaclient.base.gui.components.toppanel.TopPanel;
import com.inertiaclient.base.render.skia.CanvasWrapper;
import com.inertiaclient.base.render.yoga.YogaNode;

public class WaterMarkComponent extends YogaNode {

    public WaterMarkComponent() {
        this.styleSetHeight(TopPanel.topPanelHeight);

        var inertiaClientMod = InertiaBase.instance.getModLoader().getMods().stream().filter(inertiaMod -> inertiaMod.getId().equals("inertiaclient")).findFirst();

        final String name = inertiaClientMod.isEmpty() ? InertiaBase.CLIENT_NAME : "Inertia";
        final String version = inertiaClientMod.isEmpty() ? InertiaBase.VERSION : inertiaClientMod.get().getVersion();

        this.setRenderCallback((context, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            var inertiaTextBuilder = CanvasWrapper.getFreshTextBuilder();
            inertiaTextBuilder.basic(name, this.getWidth() / 2, this.getHeight() / 2 - 2);
            inertiaTextBuilder.setFontSize(14);
            inertiaTextBuilder.setHorizontalAlignment(CanvasWrapper.TextBuilder.HorizontalAlignment.CENTER);
            inertiaTextBuilder.setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE);
            inertiaTextBuilder.draw(canvas);

            var versionTextBuilder = CanvasWrapper.getFreshTextBuilder();
            versionTextBuilder.basic(version, this.getWidth() / 2, this.getHeight() / 2 + 7).setFontSize(8);
            versionTextBuilder.setHorizontalAlignment(CanvasWrapper.TextBuilder.HorizontalAlignment.CENTER);
            versionTextBuilder.setVerticalAlignment(CanvasWrapper.TextBuilder.VerticalAlignment.MIDDLE);
            versionTextBuilder.draw(canvas);
        });
    }

}
