package me.gravityio.fishingbutgood.mixins.impl;

import me.gravityio.fishingbutgood.mixins.inter.ModPlayer;
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

    @Override
    public List<FishingBobberEntity> fishingButGood$getBobbers() {
        return this.modBobbers;
    }
}
