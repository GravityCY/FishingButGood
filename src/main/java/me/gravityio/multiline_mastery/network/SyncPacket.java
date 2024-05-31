package me.gravityio.multiline_mastery.network;

import me.gravityio.multiline_mastery.MultilineMastery;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class SyncPacket implements FabricPacket {
    public static final PacketType<SyncPacket> TYPE = PacketType.create(new Identifier(MultilineMastery.MOD_ID, "sync"), SyncPacket::new);
    private final int angle;

    public SyncPacket(int angle) {
        this.angle = angle;
    }

    public SyncPacket(PacketByteBuf buf) {
        this.angle = buf.readInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.angle);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public int getAngle() {
        return this.angle;
    }
}
