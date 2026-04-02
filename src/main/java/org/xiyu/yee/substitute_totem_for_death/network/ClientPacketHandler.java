package org.xiyu.yee.substitute_totem_for_death.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.xiyu.yee.substitute_totem_for_death.Substitute_totem_for_death;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {
    public static void handleTotemActivation(int entityId) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        Entity entity = mc.level.getEntity(entityId);
        if (entity instanceof LivingEntity living) {
            ItemStack modTotem = new ItemStack(Substitute_totem_for_death.SUBSTITUTE_TOTEM.get());
            // 只在本地玩家屏幕上显示旋转图腾叠加动画
            if (entity == mc.player) {
                mc.gameRenderer.displayItemActivation(modTotem);
            }
            // 所有客户端都显示粒子效果
            mc.particleEngine.createTrackingEmitter(living, ParticleTypes.TOTEM_OF_UNDYING, 30);
        }
    }
}
