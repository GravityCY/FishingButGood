package me.gravityio.multiline_mastery.mixins.inter;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;

import java.util.List;

public interface ModPlayer {
    List<FishingHook> fishingButGood$getBobbers();

    int fishingButGood$getAngle();

    void fishingButGood$setAngle(int v);

    Player fishingButGood$getPlayer();
}
