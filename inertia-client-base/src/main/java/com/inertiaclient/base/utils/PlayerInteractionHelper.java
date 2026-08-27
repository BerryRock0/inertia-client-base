package com.inertiaclient.base.utils;

import com.inertiaclient.base.InertiaBase;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SwingAnimation;

public class PlayerInteractionHelper {

    public static void interactItem(Player player, InteractionHand hand) {
        InteractionResult result = InertiaBase.mc.gameMode.useItem(player, hand);
        if (result instanceof InteractionResult.Success success) {
            if (success.swingSource() == InteractionResult.SwingSource.PREDICTED) {
                ItemStack heldItem = player.getItemInHand(hand);
                SwingAnimation attackAnimation = heldItem.getAttackAnimation();
                InertiaBase.mc.player.swing(hand, attackAnimation, false);
            }
        }
    }

}
