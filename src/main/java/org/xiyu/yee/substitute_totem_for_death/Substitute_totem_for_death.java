package org.xiyu.yee.substitute_totem_for_death;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import org.xiyu.yee.substitute_totem_for_death.item.SubstituteTotemItem;
import org.xiyu.yee.substitute_totem_for_death.network.ModNetworking;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Rarity;

@Mod(Substitute_totem_for_death.MODID)
public class Substitute_totem_for_death {
    public static final String MODID = "substitute_totem_for_death";
    private static final Logger LOGGER = LogUtils.getLogger();

    // 创建物品注册器
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    
    // 创建创造模式物品栏注册器
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // 注册替死图腾物品
    public static final RegistryObject<Item> SUBSTITUTE_TOTEM = ITEMS.register("substitute_totem",
            () -> new SubstituteTotemItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)  // 设置为金色稀有度
                    .fireResistant()      // 添加防火属性，与不死图腾一致
            ));

    // 注册创造模式物品栏
    public static final RegistryObject<CreativeModeTab> SUBSTITUTE_TAB = CREATIVE_MODE_TABS.register("substitute_tab",
            () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> SUBSTITUTE_TOTEM.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(SUBSTITUTE_TOTEM.get());
                    }).build());

    public Substitute_totem_for_death() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // 注册网络通道
        ModNetworking.register();
        
        // 注册物品
        ITEMS.register(modEventBus);
        // 注册创造模式物品栏
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
