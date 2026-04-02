package org.xiyu.yee.substitute_totem_for_death.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.TotemParticle;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.xiyu.yee.substitute_totem_for_death.Substitute_totem_for_death;

@Mixin(LivingEntity.class)
public abstract class ClientLivingEntityMixin {

    @Inject(method = "handleEntityEvent", at = @At("HEAD"), cancellable = true)
    private void onHandleEntityEvent(byte id, CallbackInfo ci) {
        if (id == 35) {
            LivingEntity self = (LivingEntity) (Object) this;
            ItemStack totemStack = findSubstituteTotem(self);
            if (!totemStack.isEmpty()) {
                Minecraft mc = Minecraft.getInstance();
                mc.gameRenderer.displayItemActivation(totemStack);
                mc.particleEngine.createTrackingEmitter(self, net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING, 30);
                self.removeAllEffects();
                ci.cancel();
            }
        }
    }

    private static ItemStack findSubstituteTotem(LivingEntity entity) {
        ItemStack mainHand = entity.getMainHandItem();
        if (mainHand.is(Substitute_totem_for_death.SUBSTITUTE_TOTEM.get())) {
            return mainHand;
        }
        ItemStack offHand = entity.getOffhandItem();
        if (offHand.is(Substitute_totem_for_death.SUBSTITUTE_TOTEM.get())) {
            return offHand;
        }
        return ItemStack.EMPTY;
    }
}
