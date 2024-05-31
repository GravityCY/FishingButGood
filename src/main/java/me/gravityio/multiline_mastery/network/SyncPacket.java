package me.gravityio.multiline_mastery.network;

import me.gravityio.multiline_mastery.MultilineMastery;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class SyncPacket implements CustomPayload {
    public static final Id<SyncPacket> ID = new Id<>(new Identifier(MultilineMastery.MOD_ID, "sync"));
    public static final PacketCodec<PacketByteBuf, SyncPacket> CODEC = PacketCodec.of(SyncPacket::write, SyncPacket::new);

    private final int angle;

    public SyncPacket(int angle) {
        this.angle = angle;
    }

    public SyncPacket(PacketByteBuf buf) {
        this.angle = buf.readInt();
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(this.angle);
    }
    public int getAngle() {
        return this.angle;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
