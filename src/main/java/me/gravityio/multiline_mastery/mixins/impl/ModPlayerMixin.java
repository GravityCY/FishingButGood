package me.gravityio.multiline_mastery.mixins.impl;

import me.gravityio.multiline_mastery.ModConfig;
import me.gravityio.multiline_mastery.mixins.inter.ModPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(Player.class)
public abstract class ModPlayerMixin extends LivingEntity implements ModPlayer  {
    @Unique
    private final List<FishingHook> modBobbers = new ArrayList<>();
    @Unique
    private int angle;

    protected ModPlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public List<FishingHook> fishingButGood$getBobbers() {
        return this.modBobbers;
    }

    @Override
    public int fishingButGood$getAngle() {
        return this.level().isClientSide ? ModConfig.HANDLER.instance().angle.get() : this.angle;
    }

    @Override
    public void fishingButGood$setAngle(int v) {
        this.angle = v;
    }

    @Override
    public Player fishingButGood$getPlayer() {
        return (Player) (Object) this;
    }
}
