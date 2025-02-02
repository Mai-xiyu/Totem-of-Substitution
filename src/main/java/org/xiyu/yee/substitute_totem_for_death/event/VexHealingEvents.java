package org.xiyu.yee.substitute_totem_for_death.event;

import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.xiyu.yee.substitute_totem_for_death.Substitute_totem_for_death;
import net.minecraft.network.chat.Component;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.EntityType;

@Mod.EventBusSubscriber(modid = Substitute_totem_for_death.MODID)
public class VexHealingEvents {
    
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Vex vex)) return;
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();
        
        if (heldItem.is(Items.TOTEM_OF_UNDYING)) {
            // 在恼鬼位置生成悦灵
            Allay allay = new Allay(EntityType.ALLAY, player.level());
            allay.setPos(vex.position());
            allay.setCustomName(vex.getCustomName());
            player.level().addFreshEntity(allay);
            
            // 移除恼鬼
            vex.discard();
            heldItem.shrink(1);
            
            // 给予替死图腾
            ItemStack substituteTotem = new ItemStack(Substitute_totem_for_death.SUBSTITUTE_TOTEM.get());
            if (!player.addItem(substituteTotem)) {
                player.drop(substituteTotem, false);
            }
            
            player.sendSystemMessage(Component.translatable("message.substitute_totem_for_death.vex_healed"));
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }
} 