package me.gravityio.multiline_mastery.helper;

import me.gravityio.multiline_mastery.MultilineMasteryMod;
import me.gravityio.multiline_mastery.mixins.inter.ModFishingBobber;
import me.gravityio.multiline_mastery.mixins.inter.ModPlayer;
import me.gravityio.multiline_mastery.versioned.Common;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

//? if >=1.21 {
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
//?}


public class ModHelper {


    /**
     * Is the player looking at the bobber using the DEGREES angle
     */
    public static boolean isLookingAtBobber(ModPlayer player, FishingHook bobber, int range) {
        var degrees = Math.toDegrees(getLookAngle(player, bobber));
        return degrees < range;
    }

    // No clue what this is doing just asked ChatGPT
    public static double getLookAngle(ModPlayer player, FishingHook bobber) {
        var original = player.fishingButGood$getPlayer();
        var looking = original.getForward();
        looking = new Vec3(looking.x, 0, looking.z).normalize();

        var bobToPlayer = bobber.position().subtract(original.position());
        bobToPlayer = new Vec3(bobToPlayer.x, 0, bobToPlayer.z).normalize();
        return Math.acos(looking.dot(bobToPlayer));
    }

    /**
     * Gets the bobber the player is looking at <br>
     * If you can throw more bobbers return the closest one you're looking at <br>
     * If you can't throw more bobbers return the closest one
     */
    public static FishingHook getLookingBobber(ModPlayer player, ItemStack rodStack) {
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
    private static FishingHook getClosestLookingBobber(ModPlayer player, List<FishingHook> bobbers, boolean withinAngle) {
        if (bobbers.isEmpty()) return null;
        FishingHook closestBobber = null;
        double closest = 1000.0;

        for (FishingHook bobber : bobbers) {
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
        var multiLevel = getMultiLevel(player.fishingButGood$getPlayer().level(), rod);
        if (multiLevel == 0)
            return false;
        return multiLevel >= thrown;
    }

    /**
     * Gets the first hook thrown from the list of thrown hooks
     */
    public static FishingHook getThrownHook(ModPlayer player) {
        var bobbers = player.fishingButGood$getBobbers();
        return bobbers.isEmpty() ? null : bobbers.get(0);
    }

    public static int getTotalThrownHooks(ModPlayer player) {
        return player.fishingButGood$getBobbers().size();
    }

    public static void summonModBobber(Level world, ModPlayer player, ItemStack fishingRod) {
        //? if >=1.21 {
        int i = getEnchantmentLevel(world, Enchantments.LURE, fishingRod);
        int j = getEnchantmentLevel(world, Enchantments.LUCK_OF_THE_SEA, fishingRod);
        //?} elif =1.20.5 {
        /*int i = getEnchantmentLevel(Enchantments.LURE, fishingRod);
        int j = getEnchantmentLevel(Enchantments.LUCK_OF_THE_SEA, fishingRod);
        *///?} else {
        /*int i = getEnchantmentLevel(Enchantments.FISHING_SPEED, fishingRod);
        int j = getEnchantmentLevel(Enchantments.FISHING_LUCK, fishingRod);
        *///?}

        //? if >=1.21.4 {
        /*var bobber = new FishingHook(player.fishingButGood$getPlayer(), world, j, i);
        *///?} elif >=1.21.2 {
        /*var bobber = new FishingHook(player.fishingButGood$getPlayer(), world, j, i, fishingRod);
        *///?} else {
        var bobber = new FishingHook(player.fishingButGood$getPlayer(), world, j, i);
        //?}
        modifyModBobber(bobber, fishingRod);
        world.addFreshEntity(bobber);
    }

    public static void modifyModBobber(FishingHook bobber, ItemStack stack) {
        var modBobber = (ModFishingBobber) bobber;
        var fortune = getSeafarersFortuneLevel(bobber.level(), stack);
        modBobber.fishingButGood$setSeafarersFortune(fortune);
    }

    public static int getSeafarersFortuneLevel(Level world, ItemStack stack) {
        //? if >=1.21 {
        return getEnchantmentLevel(world, MultilineMasteryMod.SEAFARERS_FORTUNE_KEY, stack);
        //?} else {
        /*return getEnchantmentLevel(MultilineMasteryMod.SEAFARERS_FORTUNE_ENCHANT, stack);
        *///?}
    }

    public static ItemStack getFishingRod(ModPlayer player) {
        for (ItemStack handItem : Common.getHandSlots(player.fishingButGood$getPlayer())) {
            if (handItem.getItem() instanceof FishingRodItem) {
                return handItem;
            }
        }
        return null;
    }

    public static int getMultiLevel(Level world, ItemStack stack) {
        //? if >=1.21 {
        return getEnchantmentLevel(world, MultilineMasteryMod.MULTILINE_MASTERY_KEY, stack);
        //?} else {
        /*return getEnchantmentLevel(MultilineMasteryMod.MULTILINE_MASTERY_ENCHANT, stack);
        *///?}
    }

    public static void addModBobber(ModPlayer player, FishingHook bobber) {
        player.fishingButGood$getBobbers().add(bobber);
    }

    public static void removeModBobber(ModPlayer player, FishingHook bobber) {
        player.fishingButGood$getBobbers().remove(bobber);
    }

    //? if >=1.21 {

    public static Holder<Enchantment> getEnchantmentEntry(Level world, ResourceKey<Enchantment> key) {
        return getEnchantmentEntry(world.registryAccess(), key);
    }

    public static Holder.Reference<Enchantment> getEnchantmentEntry(RegistryAccess registry, ResourceKey<Enchantment> key) {
        return registry.lookupOrThrow(Registries.ENCHANTMENT).get(key).orElseThrow();
    }

    public static int getEnchantmentLevel(Level world, ResourceKey<Enchantment> key, ItemStack stack) {
        return getEnchantmentLevel(world.registryAccess(), key, stack);
    }

    public static int getEnchantmentLevel(RegistryAccess registry, ResourceKey<Enchantment> key, ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(getEnchantmentEntry(registry, key), stack);
    }

    //?} else {
    /*public static int getEnchantmentLevel(Enchantment enchantment, ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack);
    }
    *///?}


}
