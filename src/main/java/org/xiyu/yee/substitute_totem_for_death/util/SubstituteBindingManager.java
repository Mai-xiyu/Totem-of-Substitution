package org.xiyu.yee.substitute_totem_for_death.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class SubstituteBindingManager {
    private static final Map<UUID, UUID> bindingMap = new HashMap<>();
    private static final WeakHashMap<UUID, Long> bindingTimeMap = new WeakHashMap<>();
    
    public static void bindTarget(Player player, LivingEntity target) {
        bindingMap.put(player.getUUID(), target.getUUID());
        bindingTimeMap.put(player.getUUID(), System.currentTimeMillis());
    }
    
    public static UUID getBoundTarget(Player player) {
        return bindingMap.get(player.getUUID());
    }
    
    public static void clearBinding(Player player) {
        bindingMap.remove(player.getUUID());
        bindingTimeMap.remove(player.getUUID());
    }
    
    public static boolean isBindingValid(Player player) {
        Long bindTime = bindingTimeMap.get(player.getUUID());
        return bindTime != null && System.currentTimeMillis() - bindTime < 300000; // 5分钟有效期
    }
} 