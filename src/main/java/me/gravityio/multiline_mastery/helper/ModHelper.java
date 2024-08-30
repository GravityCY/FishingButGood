package me.gravityio.multiline_mastery.helper;

import me.gravityio.multiline_mastery.MultilineMasteryMod;
import me.gravityio.multiline_mastery.mixins.inter.ModFishingBobber;
import me.gravityio.multiline_mastery.mixins.inter.ModPlayer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class ModHelper {


    /**
     * Is the player looking at the bobber using the DEGREES angle
     */
    public static boolean isLookingAtBobber(ModPlayer player, FishingBobberEntity bobber, int range) {
        var degrees = Math.toDegrees(getLookAngle(player, bobber));
        return degrees < range;
    }

    // No clue what this is doing just asked ChatGPT
    public static double getLookAngle(ModPlayer player, FishingBobberEntity bobber) {
        var original = player.fishingButGood$getPlayer();
        var looking = original.getRotationVecClient();
        looking = new Vec3d(looking.x, 0, looking.z).normalize();

        var bobToPlayer = bobber.getPos().subtract(original.getPos());
        bobToPlayer = new Vec3d(bobToPlayer.x, 0, bobToPlayer.z).normalize();
        return Math.acos(looking.dotProduct(bobToPlayer));
    }

    /**
     * Gets the bobber the player is looking at <br>
     * If you can throw more bobbers return the closest one you're looking at <br>
     * If you can't throw more bobbers return the closest one
     */
    public static FishingBobberEntity getLookingBobber(ModPlayer player, ItemStack rodStack) {
        var allBobbers = player.fishingButGood$getBobbers();
        if (allBobbers.isEmpty()) return null;

        if (ModHelper.canCastBobber(player, rodStack)) {
            return getClosestLookingBobber(player, allBobbers, true);
        } else {
            return getClosestLookingBobber(player, allBobbers, false);
        }
    }

    /**
     * Given a list of bobbers return the one that is closest in regard to Y view angle
     */
    private static FishingBobberEntity getClosestLookingBobber(ModPlayer player, List<FishingBobberEntity> bobbers, boolean withinAngle) {
        if (bobbers.isEmpty()) return null;
        FishingBobberEntity closestBobber = null;
        double closest = 1000.0;

        for (FishingBobberEntity bobber : bobbers) {
            var lookAngle = Math.toDegrees(getLookAngle(player, bobber));
            if (lookAngle > closest || (withinAngle && lookAngle > player.fishingButGood$getAngle())) continue;
            closest = lookAngle;
            closestBobber = bobber;
        }
        return closestBobber;
    }

    /**
     * If the player has thrown no hooks or if the player has a multi level that allows for more hooks
     */
    public static boolean canCastBobber(ModPlayer player, ItemStack rod) {
        var thrown = getTotalThrownHooks(player);
        if (thrown == 0) return true;
        var multiLevel = getMultiLevel(player.fishingButGood$getPlayer().getWorld(), rod);
        if (multiLevel == 0)
            return false;
        return multiLevel >= thrown;
    }

    /**
     * Gets the first hook thrown from the list of thrown hooks
     */
    public static FishingBobberEntity getThrownHook(ModPlayer player) {
        var bobbers = player.fishingButGood$getBobbers();
        return bobbers.isEmpty() ? null : bobbers.get(0);
    }

    public static int getTotalThrownHooks(ModPlayer player) {
        return player.fishingButGood$getBobbers().size();
    }

    public static void summonModBobber(World world, ModPlayer player, ItemStack fishingRod) {
        int i = getEnchantmentLevel(world, Enchantments.LURE, fishingRod);
        int j = getEnchantmentLevel(world, Enchantments.LUCK_OF_THE_SEA, fishingRod);
        var bobber = new FishingBobberEntity(player.fishingButGood$getPlayer(), world, j, i);
        modifyModBobber(bobber, fishingRod);
        world.spawnEntity(bobber);
    }

    public static void modifyModBobber(FishingBobberEntity bobber, ItemStack stack) {
        var modBobber = (ModFishingBobber) bobber;
        var fortune = getSeafarersFortuneLevel(bobber.getWorld(), stack);
        modBobber.fishingButGood$setSeafarersFortune(fortune);
    }

    public static int getSeafarersFortuneLevel(World world, ItemStack stack) {
        return getEnchantmentLevel(world, MultilineMasteryMod.SEAFARERS_FORTUNE_KEY, stack);
    }

    public static ItemStack getFishingRod(ModPlayer player) {
        for (ItemStack handItem : player.fishingButGood$getPlayer().getHandItems()) {
            if (handItem.getItem() instanceof FishingRodItem) {
                return handItem;
            }
        }
        return null;
    }

    public static int getMultiLevel(World world, ItemStack stack) {
        return getEnchantmentLevel(world, MultilineMasteryMod.MULTILINE_MASTERY_KEY, stack);
    }

    public static void addModBobber(ModPlayer player, FishingBobberEntity bobber) {
        player.fishingButGood$getBobbers().add(bobber);
    }

    public static void removeModBobber(ModPlayer player, FishingBobberEntity bobber) {
        player.fishingButGood$getBobbers().remove(bobber);
    }

    public static RegistryEntry.Reference<Enchantment> getEnchantmentEntry(World world, RegistryKey<Enchantment> key) {
        return getEnchantmentEntry(world.getRegistryManager(), key);
    }

    public static RegistryEntry.Reference<Enchantment> getEnchantmentEntry(DynamicRegistryManager registry, RegistryKey<Enchantment> key) {
        return registry.get(RegistryKeys.ENCHANTMENT).getEntry(key).orElseThrow();
    }

    public static int getEnchantmentLevel(World world, RegistryKey<Enchantment> key, ItemStack stack) {
        return getEnchantmentLevel(world.getRegistryManager(), key, stack);
    }

    public static int getEnchantmentLevel(DynamicRegistryManager registry, RegistryKey<Enchantment> key, ItemStack stack) {
        return EnchantmentHelper.getLevel(getEnchantmentEntry(registry, key), stack);
    }



}
