package me.gravityio.multiline_mastery.mixins.impl;

import com.llamalad7.mixinextras.sugar.Local;
import me.gravityio.multiline_mastery.MultilineMasteryMod;
import me.gravityio.multiline_mastery.helper.ModHelper;
import me.gravityio.multiline_mastery.mixins.inter.ModFishingBobber;
import me.gravityio.multiline_mastery.mixins.inter.ModPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
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

@Mixin(FishingHook.class)
public abstract class FishingBobberMixin extends Projectile implements ModFishingBobber {

    @Shadow
    public abstract @Nullable Player getPlayerOwner();

    @Unique
    private int seafarersFortune;

    public FishingBobberMixin(EntityType<? extends Projectile> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void fishingButGood$setSeafarersFortune(int level) {
        this.seafarersFortune = level;
    }

    @Inject(
            method = "retrieve",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/advancements/critereon/FishingRodHookedTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/projectile/FishingHook;Ljava/util/Collection;)V",
                    ordinal = 1
            )
    )
    private void onUseAddSeafarersFortune(ItemStack stack, CallbackInfoReturnable<Integer> cir, @Local LootParams lootContextParameterSet, @Local LootTable lootTable, @Local List<ItemStack> items) {
        if (this.seafarersFortune == 0) return;

        var bound = this.random.nextInt(this.seafarersFortune) + 1;
        for (int x = 0; x < bound; x++) {
            items.addAll(lootTable.getRandomItems(lootContextParameterSet));
        }
    }

    @Inject(method = "updateOwnerInfo", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;fishing:Lnet/minecraft/world/entity/projectile/FishingHook;"), cancellable = true)
    private void setModdedPlayerFishHook(@Nullable FishingHook hook, CallbackInfo ci) {
        var self = (FishingHook) (Object) this;
        var player = this.getPlayerOwner();
        var modPlayer = (ModPlayer) player;

        var thrown = ModHelper.getTotalThrownHooks(modPlayer);
        if (hook == null) {
            if (thrown == 1)
                player.fishing = null;
            MultilineMasteryMod.DEBUG("Removing Mod Bobber");
            ModHelper.removeModBobber(modPlayer, self);
        } else {
            if (thrown == 0) {
                player.fishing = hook;
            }
            MultilineMasteryMod.DEBUG("Adding Mod Bobber");
            ModHelper.addModBobber(modPlayer, hook);
        }
        ci.cancel();
    }


}
