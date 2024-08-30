package me.gravityio.multiline_mastery.mixins.impl;

import me.gravityio.multiline_mastery.MultilineMasteryMod;
import me.gravityio.multiline_mastery.helper.ModHelper;
import me.gravityio.multiline_mastery.mixins.inter.ModPlayer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(FishingRodItem.class)
public class FishingRodMixin extends Item {
    public FishingRodMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack stack = player.getStackInHand(hand);
        ModPlayer modPlayer = (ModPlayer) player;

        EquipmentSlot slot = hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;

        var looking = ModHelper.getLookingBobber(modPlayer, stack);
        if (looking == null) {
            if (ModHelper.canCastBobber(modPlayer, stack)) {
                if (world.isClient) {
                    world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
                    player.incrementStat(Stats.USED.getOrCreateStat(this));
                    player.emitGameEvent(GameEvent.ITEM_INTERACT_START);
                    cir.setReturnValue(TypedActionResult.success(stack, true));
                    return;
                }
                MultilineMasteryMod.DEBUG("Casting a Hook!");
                ModHelper.summonModBobber(world, modPlayer, stack);
            }
        } else {
            if (world.isClient) {
                world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
                player.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
                cir.setReturnValue(TypedActionResult.success(stack, true));
                return;
            }
            MultilineMasteryMod.DEBUG("Using Hook player is looking at!");
            int i = looking.use(stack);
            stack.damage(i, player, slot);
        }
        cir.setReturnValue(TypedActionResult.success(stack, world.isClient()));
    }
}
