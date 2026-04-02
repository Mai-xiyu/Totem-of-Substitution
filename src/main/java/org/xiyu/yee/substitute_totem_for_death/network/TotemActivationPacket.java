package org.xiyu.yee.substitute_totem_for_death.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TotemActivationPacket {
    private final int entityId;

    public TotemActivationPacket(int entityId) {
        this.entityId = entityId;
    }

    public static void encode(TotemActivationPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
    }

    public static TotemActivationPacket decode(FriendlyByteBuf buf) {
        return new TotemActivationPacket(buf.readInt());
    }

    public static void handle(TotemActivationPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> ClientPacketHandler.handleTotemActivation(msg.entityId));
        });
        ctx.get().setPacketHandled(true);
    }
}
