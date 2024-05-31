package me.gravityio.multiline_mastery.mixins.impl;

import me.gravityio.multiline_mastery.MultilineMastery;
import me.gravityio.multiline_mastery.helper.ModHelper;
import me.gravityio.multiline_mastery.mixins.inter.ModFishingBobber;
import me.gravityio.multiline_mastery.mixins.inter.ModPlayer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberMixin extends ProjectileEntity implements ModFishingBobber {

    @Shadow
    public abstract @Nullable PlayerEntity getPlayerOwner();

    @Unique
    private int seafarersFortune;

    public FishingBobberMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void fishingButGood$setSeafarersFortune(int level) {
        this.seafarersFortune = level;
    }

    @Inject(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/advancement/criterion/FishingRodHookedCriterion;trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/projectile/FishingBobberEntity;Ljava/util/Collection;)V",
                    ordinal = 1
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void onUseAddSeafarersFortune(ItemStack usedItem, CallbackInfoReturnable<Integer> cir, PlayerEntity playerEntity, int i, LootContextParameterSet lootContextParameterSet, LootTable lootTable, List<ItemStack> items) {
        if (this.seafarersFortune == 0) return;

        var bound = this.random.nextInt(this.seafarersFortune) + 1;
        for (int x = 0; x < bound; x++) {
            items.addAll(lootTable.generateLoot(lootContextParameterSet));
        }
    }

    @Inject(method = "setPlayerFishHook", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;fishHook:Lnet/minecraft/entity/projectile/FishingBobberEntity;"), cancellable = true)
    private void setModdedPlayerFishHook(@Nullable FishingBobberEntity fishingBobber, CallbackInfo ci) {
        var self = (FishingBobberEntity) (Object) this;
        var player = this.getPlayerOwner();
        var modPlayer = (ModPlayer) player;

        var thrown = ModHelper.getTotalThrownHooks(modPlayer);
        if (fishingBobber == null) {
            if (thrown == 1)
                player.fishHook = null;
            MultilineMastery.DEBUG("Removing Mod Bobber");
            ModHelper.removeModBobber(modPlayer, self);
        } else {
            if (thrown == 0) {
                player.fishHook = fishingBobber;
            }
            MultilineMastery.DEBUG("Adding Mod Bobber");
            ModHelper.addModBobber(modPlayer, fishingBobber);
        }
        ci.cancel();
    }


}
