package me.gravityio.fishingbutgood;

import me.gravityio.fishingbutgood.mixins.inter.ModFishingBobber;
import me.gravityio.fishingbutgood.mixins.inter.ModPlayer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Helper {
    public static final int DEGREES = 10;

    /**
     * Is the player looking at the bobber using the DEGREES angle
     */
    public static boolean isLookingAtBobber(PlayerEntity player, FishingBobberEntity bobber) {
        var degrees = Math.toDegrees(getLookAngle(player, bobber));
        return degrees < DEGREES;
    }

    // No cluue what this is doing just asked chatgpt
    public static double getLookAngle(PlayerEntity player, FishingBobberEntity bobber) {
        var looking = player.getRotationVecClient();
        looking = new Vec3d(looking.x, 0, looking.z).normalize();

        var bobToPlayer = bobber.getPos().subtract(player.getPos());
        bobToPlayer = new Vec3d(bobToPlayer.x, 0, bobToPlayer.z).normalize();
        return Math.acos(looking.dotProduct(bobToPlayer));
    }

    /**
     * Gets the bobber the player is looking at
     */
    public static FishingBobberEntity getLookingBobber(PlayerEntity player) {
        var modPlayer = (ModPlayer) player;

        var all = modPlayer.fishingButGood$getBobbers();
        if (all.isEmpty()) return null;

        for (FishingBobberEntity bobber : all) {
            if (isLookingAtBobber(player, bobber))
                return bobber;
        }
        return null;
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
        var fishesLoveMe = getFishesLoveMeLevel(stack);
        modBobber.fishingButGood$setSeafarersFortune(fishesLoveMe);
    }

    public static int getFishesLoveMeLevel(ItemStack stack) {
        return EnchantmentHelper.getLevel(FishingButGood.SEAFARERS_FORTUNE_ENCHANT, stack);
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
