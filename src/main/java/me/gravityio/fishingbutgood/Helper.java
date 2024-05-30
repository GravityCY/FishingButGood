package me.gravityio.fishingbutgood;

import me.gravityio.fishingbutgood.mixins.inter.ModFishingBobber;
import me.gravityio.fishingbutgood.mixins.inter.ModPlayer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class Helper {
    public static final int RANGE = 15;

    /**
     * Is the player looking at the bobber using the DEGREES angle
     */
    public static boolean isLookingAtBobber(PlayerEntity player, FishingBobberEntity bobber, int range) {
        var degrees = Math.toDegrees(getLookAngle(player, bobber));
        return degrees < range;
    }

    // No clue what this is doing just asked ChatGPT
    public static double getLookAngle(PlayerEntity player, FishingBobberEntity bobber) {
        var looking = player.getRotationVecClient();
        looking = new Vec3d(looking.x, 0, looking.z).normalize();

        var bobToPlayer = bobber.getPos().subtract(player.getPos());
        bobToPlayer = new Vec3d(bobToPlayer.x, 0, bobToPlayer.z).normalize();
        return Math.acos(looking.dotProduct(bobToPlayer));
    }

    /**
     * Gets the bobber the player is looking at <br>
     * If you can throw more bobbers return the one you're looking at <br>
     * If you can't throw more bobbers return the closest one
     */
    public static FishingBobberEntity getLookingBobber(PlayerEntity player, ItemStack rodStack) {
        var modPlayer = (ModPlayer) player;

        var allBobbers = modPlayer.fishingButGood$getBobbers();
        if (allBobbers.isEmpty()) return null;

        if (Helper.canCastBobber(player, rodStack)) {
            return getLookingBobber(player, allBobbers);
        } else {
            return getClosestLookingBobber(player, allBobbers);
        }
    }

    public static FishingBobberEntity getLookingBobber(PlayerEntity player) {
        var modPlayer = (ModPlayer) player;
        var allBobbers = modPlayer.fishingButGood$getBobbers();
        return getLookingBobber(player, allBobbers);
    }

    private static FishingBobberEntity getLookingBobber(PlayerEntity player, List<FishingBobberEntity> bobbers) {
        if (bobbers.isEmpty()) return null;
        for (FishingBobberEntity bobber : bobbers) {
            if (isLookingAtBobber(player, bobber, RANGE))
                return bobber;
        }
        return null;
    }

    /**
     * Given a list of bobbers return the one that is closest in regard to Y view angle
     */
    private static FishingBobberEntity getClosestLookingBobber(PlayerEntity player, List<FishingBobberEntity> bobbers) {
        if (bobbers.isEmpty()) return null;
        FishingBobberEntity closestBobber = null;
        double closest = 1000.0;

        for (FishingBobberEntity bobber : bobbers) {
            var lookAngle = getLookAngle(player, bobber);
            if (lookAngle > closest) continue;
            closest = lookAngle;
            closestBobber = bobber;
        }
        return closestBobber;
    }

    /**
     * If the player has thrown no hooks or if the player has a multi level that allows for more hooks
     */
    public static boolean canCastBobber(PlayerEntity player, ItemStack rod) {
        var thrown = getTotalThrownHooks(player);
        if (thrown == 0) return true;
        var multiLevel = getMultiLevel(rod);
        if (multiLevel == 0)
            return false;
        return multiLevel >= thrown;
    }

    /**
     * Gets the first hook thrown from the list of thrown hooks
     */
    public static FishingBobberEntity getThrownHook(PlayerEntity player) {
        return getThrownHook((ModPlayer) player);
    }

    /**
     * Gets the first hook thrown from the list of thrown hooks
     */
    public static FishingBobberEntity getThrownHook(ModPlayer player) {
        var bobbers = player.fishingButGood$getBobbers();
        return bobbers.isEmpty() ? null : bobbers.get(0);
    }

    public static int getTotalThrownHooks(PlayerEntity player) {
        return getTotalThrownHooks((ModPlayer) player);
    }

    public static int getTotalThrownHooks(ModPlayer player) {
        return player.fishingButGood$getBobbers().size();
    }

    public static void summonModBobber(World world, PlayerEntity player, ItemStack fishingRod) {
        int i = EnchantmentHelper.getLure(fishingRod);
        int j = EnchantmentHelper.getLuckOfTheSea(fishingRod);
        var bobber = new FishingBobberEntity(player, world, j, i);
        modifyModBobber(bobber, fishingRod);
        world.spawnEntity(bobber);
    }

    public static void modifyModBobber(FishingBobberEntity bobber, ItemStack stack) {
        var modBobber = (ModFishingBobber) bobber;
        var fortune = getSeafarersFortuneLevel(stack);
        modBobber.fishingButGood$setSeafarersFortune(fortune);
    }

    public static int getSeafarersFortuneLevel(ItemStack stack) {
        return EnchantmentHelper.getLevel(FishingButGood.SEAFARERS_FORTUNE_ENCHANT, stack);
    }
    public static ItemStack getFishingRod(PlayerEntity player) {
        for (ItemStack handItem : player.getHandItems()) {
            if (handItem.getItem() instanceof FishingRodItem) {
                return handItem;
            }
        }
        return null;
    }

    public static int getMultiLevel(ItemStack stack) {
        return EnchantmentHelper.getLevel(FishingButGood.MULTILINE_MASTERY_ENCHANT, stack);
    }

    public static void addModBobber(PlayerEntity player, FishingBobberEntity bobber) {
        var modPlayer = (ModPlayer) player;
        modPlayer.fishingButGood$getBobbers().add(bobber);
    }

    public static void removeModBobber(PlayerEntity player, FishingBobberEntity bobber) {
        var modPlayer = (ModPlayer) player;
        modPlayer.fishingButGood$getBobbers().remove(bobber);
    }
}
