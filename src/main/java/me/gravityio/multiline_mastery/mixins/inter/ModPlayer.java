package me.gravityio.multiline_mastery.mixins.inter;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;

import java.util.List;

public interface ModPlayer {
    List<FishingBobberEntity> fishingButGood$getBobbers();

    int fishingButGood$getAngle();

    void fishingButGood$setAngle(int v);

    PlayerEntity fishingButGood$getPlayer();
}
