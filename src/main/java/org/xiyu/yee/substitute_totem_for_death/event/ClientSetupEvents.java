package org.xiyu.yee.substitute_totem_for_death.event;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.xiyu.yee.substitute_totem_for_death.Substitute_totem_for_death;

@Mod.EventBusSubscriber(modid = Substitute_totem_for_death.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetupEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(Substitute_totem_for_death.SUBSTITUTE_TOTEM.get(),
                    new ResourceLocation("using"), (stack, level, entity, seed) -> {
                        return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
                    });
        });
    }
}
