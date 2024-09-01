package me.gravityio.multiline_mastery.network;

import me.gravityio.multiline_mastery.MultilineMasteryMod;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;

//? if >=1.20.5 {
/*import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
*///?}

//? if >=1.20.5 {
/*public class SyncPacket implements CustomPacketPayload {
    public static final Type<SyncPacket> TYPE = new Type<>(MultilineMasteryMod.id("sync"));
    public static final StreamCodec<FriendlyByteBuf, SyncPacket> CODEC = StreamCodec.ofMember(SyncPacket::write, SyncPacket::new);
*///?} else {
public class SyncPacket implements FabricPacket {
    public static final PacketType<SyncPacket> TYPE = PacketType.create(MultilineMasteryMod.id("sync"), SyncPacket::new);
//?}
    private final int angle;

    public SyncPacket(int angle) {
        this.angle = angle;
    }

    public SyncPacket(FriendlyByteBuf buf) {
        this.angle = buf.readInt();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.angle);
    }

    public int getAngle() {
        return this.angle;
    }

    //? if >=1.20.5 {
    /*@Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    *///?} else {
    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
    //?}
}
