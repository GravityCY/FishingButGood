package me.gravityio.multiline_mastery.mixins.impl;

import me.gravityio.multiline_mastery.mixins.inter.ModPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerEntity.class)
public class ModPlayerMixin implements ModPlayer {
    @Unique
    List<FishingBobberEntity> modBobbers = new ArrayList<>();
    @Unique
    private int angle;

    @Override
    public List<FishingBobberEntity> fishingButGood$getBobbers() {
        return this.modBobbers;
    }

    @Override
    public int fishingButGood$getAngle() {
        return this.angle;
    }

    @Override
    public void fishingButGood$setAngle(int v) {
        this.angle = v;
    }

    @Override
    public PlayerEntity fishingButGood$getPlayer() {
        return (PlayerEntity) (Object) this;
    }
}
