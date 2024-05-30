package me.gravityio.fishingbutgood.mixins.impl;

import me.gravityio.fishingbutgood.FishingButGood;
import me.gravityio.fishingbutgood.Helper;
import net.minecraft.client.network.ClientPlayerEntity;
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
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(FishingRodItem.class)
public class FishingRodMixin extends Item {
    public FishingRodMixin(Settings settings) {
        super(settings);
    }

    /**
     * Fishing rods can cast multiple hooks
     * @author GravityIO
     * @reason Redesign the way Fishing Rods work
     */
    @Overwrite
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        var looking = Helper.getLookingBobber(player, stack);
        if (looking == null) {
            if (Helper.canCastBobber(player, stack)) {
                if (world.isClient) {
                    world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
                    player.incrementStat(Stats.USED.getOrCreateStat(this));
                    player.emitGameEvent(GameEvent.ITEM_INTERACT_START);
                    return TypedActionResult.success(stack, true);
                }
                FishingButGood.DEBUG("Casting a Hook!");
                Helper.summonModBobber(world, player, stack);
            }
        } else {
            if (world.isClient) {
                world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
                player.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
                return TypedActionResult.success(stack, true);
            }
            FishingButGood.DEBUG("Using Hook player is looking at!");
            int i = looking.use(stack);
            stack.damage(i, player, p -> p.sendToolBreakStatus(hand));
        }
        return TypedActionResult.success(stack, world.isClient());
    }
}
