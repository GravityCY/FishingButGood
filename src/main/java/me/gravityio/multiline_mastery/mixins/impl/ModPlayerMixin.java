package me.gravityio.multiline_mastery.mixins.impl;

import me.gravityio.multiline_mastery.ModConfig;
import me.gravityio.multiline_mastery.mixins.inter.ModPlayer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class ModPlayerMixin extends LivingEntity implements ModPlayer  {
    @Unique
    List<FishingBobberEntity> modBobbers = new ArrayList<>();
    @Unique
    private int angle;

    protected ModPlayerMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public List<FishingBobberEntity> fishingButGood$getBobbers() {
        return this.modBobbers;
    }

    @Override
    public int fishingButGood$getAngle() {
        return this.getWorld().isClient ? ModConfig.HANDLER.instance().angle.get() : this.angle;
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
