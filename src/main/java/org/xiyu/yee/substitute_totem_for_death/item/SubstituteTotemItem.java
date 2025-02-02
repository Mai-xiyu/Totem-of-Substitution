package org.xiyu.yee.substitute_totem_for_death.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import org.xiyu.yee.substitute_totem_for_death.util.SubstituteBindingManager;

public class SubstituteTotemItem extends Item {
    public SubstituteTotemItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.substitute_totem_for_death.substitute_totem.desc"));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            SubstituteBindingManager.bindTarget(player, target);
            player.sendSystemMessage(Component.translatable("message.substitute_totem_for_death.bind_success"));
        }
        return true;
    }
} 