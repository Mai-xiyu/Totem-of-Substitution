package org.xiyu.yee.substitute_totem_for_death.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.xiyu.yee.substitute_totem_for_death.Substitute_totem_for_death;
import net.minecraft.world.entity.Entity;
import org.xiyu.yee.substitute_totem_for_death.util.SubstituteBindingManager;
import java.util.List;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.resources.ResourceLocation;
import org.xiyu.yee.substitute_totem_for_death.network.ModNetworking;
import org.xiyu.yee.substitute_totem_for_death.network.TotemActivationPacket;

import net.minecraft.world.entity.animal.SnowGolem;

import net.minecraft.advancements.Advancement;

@Mod.EventBusSubscriber(modid = Substitute_totem_for_death.MODID)
public class SubstituteTotemEvents {
    private static final double SEARCH_RADIUS = 10.0D;
    private static final double BOSS_SEARCH_RADIUS = 20.0D;  // Boss战时的更大搜索范围
    private static boolean isTransferring = false; // 防止伤害转移递归
    
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (isTransferring) return;
        if (!(event.getEntity() instanceof Player player)) return;
        
        // 检查玩家是否持有替死图腾
        ItemStack totem = findTotem(player);
        if (totem.isEmpty()) return;
        
        // 检查伤害是否致命
        float remainingHealth = player.getHealth() - event.getAmount();
        boolean isFatalDamage = remainingHealth <= 0;
        
        // 只有在致命伤害时才转移
        if (!isFatalDamage) return;
        
        // 寻找替死目标
        LivingEntity target = findSubstituteTarget(player);
        if (target != null) {
            // 播放不死图腾的音效和粒子效果
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);

            if (player instanceof ServerPlayer serverPlayer) {
                // 发送自定义网络包触发模组图腾动画（避免原版 event 35 硬编码使用原版纹理）
                ModNetworking.sendToTrackingAndSelf(player, new TotemActivationPacket(player.getId()));
                
                // 添加不死图腾的状态效果
                player.setHealth(1.0F);
                player.removeAllEffects();
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
            }

            // 转移伤害并消耗图腾
            totem.shrink(1);  // 致命伤害时一定消耗图腾
            event.setCanceled(true);
            try {
                isTransferring = true;
                target.hurt(event.getSource(), event.getAmount());
            } finally {
                isTransferring = false;
            }
            SubstituteBindingManager.clearBinding(player); // 清除已使用的绑定

            // 授予 used_totem 进度条件
            if (player instanceof ServerPlayer serverPlayer) {
                Advancement mainAdv = serverPlayer.server.getAdvancements()
                    .getAdvancement(new ResourceLocation("substitute_totem_for_death:substitute_totem"));
                if (mainAdv != null) {
                    serverPlayer.getAdvancements().award(mainAdv, "used_totem");
                }
            }

            // 检查进度条件
            if (event.getAmount() >= 15.0F) {
                if (player instanceof ServerPlayer serverPlayer) {
                    Advancement advancement = serverPlayer.server.getAdvancements()
                        .getAdvancement(new ResourceLocation("substitute_totem_for_death:substitute_totem_damage"));
                    if (advancement != null) {
                        serverPlayer.getAdvancements().award(advancement, "high_damage");
                    }
                }
            }

            if (target instanceof Player) {
                if (player instanceof ServerPlayer serverPlayer) {
                    Advancement advancement = serverPlayer.server.getAdvancements()
                        .getAdvancement(new ResourceLocation("substitute_totem_for_death:substitute_totem_save"));
                    if (advancement != null) {
                        serverPlayer.getAdvancements().award(advancement, "save_player");
                    }
                }
            }
        }
    }
    
    private static ItemStack findTotem(Player player) {
        // 检查主手
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.is(Substitute_totem_for_death.SUBSTITUTE_TOTEM.get())) {
            return mainHand;
        }
        
        // 检查副手
        ItemStack offHand = player.getOffhandItem();
        if (offHand.is(Substitute_totem_for_death.SUBSTITUTE_TOTEM.get())) {
            return offHand;
        }
        
        return ItemStack.EMPTY;
    }
    
    private static boolean hasSoul(LivingEntity entity) {
        return !(entity instanceof IronGolem) && !(entity instanceof SnowGolem);
    }

    private static LivingEntity findSubstituteTarget(Player player) {
        // 首先检查是否有绑定目标
        UUID boundTargetId = SubstituteBindingManager.getBoundTarget(player);
        if (boundTargetId != null && SubstituteBindingManager.isBindingValid(player)) {
            if (player.level() instanceof ServerLevel serverLevel) {
                Entity boundEntity = serverLevel.getEntity(boundTargetId);
                if (boundEntity instanceof LivingEntity living && boundEntity.isAlive() && hasSoul(living)) {
                    return living;
                }
            }
            return null; // 如果有绑定但目标无效，则不转移伤害
        }
        
        // 如果没有绑定目标，寻找范围内的随机生物
        // 检查是否在Boss战中
        double searchRadius = SEARCH_RADIUS;
        List<LivingEntity> bosses = player.level().getEntitiesOfClass(
            LivingEntity.class,
            player.getBoundingBox().inflate(BOSS_SEARCH_RADIUS),
            entity -> entity instanceof EnderDragon || entity instanceof WitherBoss
        );
        if (!bosses.isEmpty()) {
            searchRadius = BOSS_SEARCH_RADIUS;
        }
        
        // 寻找范围内的其他生物
        List<LivingEntity> nearbyEntities = player.level().getEntitiesOfClass(
            LivingEntity.class,
            player.getBoundingBox().inflate(searchRadius),
            entity -> entity != player && entity.isAlive() && hasSoul(entity)
        );
        
        if (!nearbyEntities.isEmpty()) {
            return nearbyEntities.get(player.getRandom().nextInt(nearbyEntities.size()));
        }
        
        return null;
    }


} 