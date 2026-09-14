package com.inertiaclient.base.gui.components.module.values.entitycolor;

import com.inertiaclient.base.gui.ModernClickGui;
import com.inertiaclient.base.gui.components.MainFrame;
import com.inertiaclient.base.gui.components.TextLabel;
import com.inertiaclient.base.gui.components.module.values.color.ColorContainer;
import com.inertiaclient.base.gui.components.module.values.color.ColorContainerInterface;
import com.inertiaclient.base.render.skia.SkiaUtils;
import com.inertiaclient.base.render.yoga.ButtonIdentifier;
import com.inertiaclient.base.render.yoga.YogaNode;
import com.inertiaclient.base.render.yoga.layouts.AlignItems;
import com.inertiaclient.base.render.yoga.layouts.GapGutter;
import com.inertiaclient.base.value.WrappedColor;
import com.inertiaclient.base.value.impl.EntityTypeColorValue;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.MobCategory;

import java.awt.Color;

public class GroupComponent extends YogaNode {

    public GroupComponent(EntityTypeColorValue entityTypeColorValue, MobCategory spawnGroup) {
        this.setSearchContext(Component.translatable("icb.gui.pages.entitycolor.spawn_group_color_label", spawnGroup.name()).getString());
        this.styleSetHeight(12);
        this.styleSetAlignItems(AlignItems.CENTER);
        this.styleSetGap(GapGutter.COLUMN, 5);
        this.setHoverCursorToIndicateClick();

        this.setReleaseClickCallback((relativeMouseX, relativeMouseY, button, clickType) -> {
            if (button == ButtonIdentifier.LEFT) {
                WrappedColor color = entityTypeColorValue.getValue().getSpawnGroupColor(spawnGroup);
                ModernClickGui.MODERN_CLICK_GUI.getRoot().addChild(new ColorContainer(color, new ColorContainerInterface() {
                    @Override
                    public String getNameHeader() {
                        return spawnGroup.getName();
                    }

                    @Override
                    public WrappedColor getDefault() {
                        return entityTypeColorValue.getDefaultValue().getSpawnGroupColor(spawnGroup);
                    }

                    @Override
                    public void setColor(WrappedColor wrappedColor) {
                        entityTypeColorValue.getValue().setSpawnGroupColor(spawnGroup, wrappedColor);
                    }
                }, () -> this.getGlobalX() + relativeMouseX, () -> this.getGlobalY()));
                return true;

            }
            return false;
        });

        YogaNode colorDisplay = new YogaNode();
        this.addChild(colorDisplay);
        colorDisplay.styleSetWidth(10);
        colorDisplay.styleSetHeight(10);
        colorDisplay.setRenderCallback((graphics, globalMouseX, globalMouseY, relativeMouseX, relativeMouseY, delta, canvas) -> {
            canvas.drawRect(0, 0, colorDisplay.getWidth(), colorDisplay.getHeight(), entityTypeColorValue.getValue().getSpawnGroupColor(spawnGroup).getRenderColor());
            try (var stroke = SkiaUtils.createStrokePaint(Color.black, .3f)) {
                canvas.drawRect(0, 0, colorDisplay.getWidth(), colorDisplay.getHeight(), stroke);
            }
        });

        var label = new TextLabel(Component.translatable("icb.gui.pages.entitycolor.spawn_group_color_label", spawnGroup.getName()));
        label.setFontSize(() -> 8f).setColor(() -> this.shouldShowHoveredEffects() ? MainFrame.s_selectedTextColor.get() : MainFrame.s_unselectedTextColor.get());

        this.addChild(label);

    }

}
