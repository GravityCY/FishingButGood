package me.gravityio.multiline_mastery.mixins.impl;

import me.gravityio.multiline_mastery.MultilineMasteryMod;
import me.gravityio.multiline_mastery.helper.ModHelper;
import me.gravityio.multiline_mastery.mixins.inter.ModPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if >=1.21.2 {
import net.minecraft.world.InteractionResult;
//?} else {
/*import net.minecraft.world.InteractionResultHolder;
*///?}

@Mixin(net.minecraft.world.item.FishingRodItem.class)
public class FishingRodMixin extends Item {

    public FishingRodMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    //? if >=1.21.2 {
    public void use(Level world, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
    //?} else {
    /*public void use(Level world, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
    *///?}
        ItemStack stack = player.getItemInHand(hand);
        ModPlayer modPlayer = (ModPlayer) player;

        EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;

        var looking = ModHelper.getLookingBobber(modPlayer, stack);
        if (looking == null) {
            if (ModHelper.canCastBobber(modPlayer, stack)) {
                if (world.isClientSide) {
                    world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
                    player.awardStat(Stats.ITEM_USED.get(this));
                    player.gameEvent(GameEvent.ITEM_INTERACT_START);
                    //? if >=1.21.2 {
                    cir.setReturnValue(InteractionResult.SUCCESS);
                    //?} else {
                    /*cir.setReturnValue(InteractionResultHolder.sidedSuccess(stack, true));
                    *///?}
                    return;
                }
                MultilineMasteryMod.DEBUG("Casting a Hook!");
                ModHelper.summonModBobber(world, modPlayer, stack);
            }
        } else {
            if (world.isClientSide) {
                world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
                player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
                //? if >=1.21.2 {
                cir.setReturnValue(InteractionResult.SUCCESS);
                //?} else {
                /*cir.setReturnValue(InteractionResultHolder.sidedSuccess(stack, true));
                *///?}
                return;
            }
            MultilineMasteryMod.DEBUG("Using Hook player is looking at!");
            int i = looking.retrieve(stack);
            //? if >=1.20.5 {
            stack.hurtAndBreak(i, player, slot);
            //?} else {
            /*stack.hurtAndBreak(i, player, player1 -> player1.broadcastBreakEvent(slot));
            *///?}
        }
        //? if >=1.21.2 {
        cir.setReturnValue(InteractionResult.SUCCESS);
        //?} else {
        /*cir.setReturnValue(InteractionResultHolder.sidedSuccess(stack, world.isClientSide));
        *///?}
    }
}
