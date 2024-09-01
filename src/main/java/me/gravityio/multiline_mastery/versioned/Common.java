package me.gravityio.multiline_mastery.versioned;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.function.Consumer;

//? if >=1.21 {
/*import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
*///?}

public class Common {

    public static ResourceLocation parse(String all) {
        //? if >=1.21 {
        /*return ResourceLocation.parse(all);
        *///?} else {
        return new ResourceLocation(all);
        //?}
    }

    public static ResourceLocation parse(String namespace, String path) {
        //? if >=1.21 {
        /*return ResourceLocation.fromNamespaceAndPath(namespace, path);
        *///?} else {
        return new ResourceLocation(namespace, path);
        //?}
    }

    //? if >=1.21 {
    /*public static void addToLootable(LootPool.Builder pool, HolderLookup.Provider registries, ResourceKey<Enchantment> enchantmentKey, NumberProvider numberProvider, Consumer<LootPoolSingletonContainer.Builder<?>> modify) {
        var registry = registries.lookupOrThrow(Registries.ENCHANTMENT);
        var enchant = registry.get(enchantmentKey).orElseThrow();
        var item = LootItem.lootTableItem(Items.BOOK).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchant, numberProvider));
        modify.accept(item);
        pool.add(item);
    }
    *///?} else {
    public static void addToLootable(LootPool.Builder pool, Enchantment enchantment, NumberProvider numberProvider, Consumer<LootPoolSingletonContainer.Builder<?>> modify) {
        var item = LootItem.lootTableItem(Items.BOOK).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchantment, numberProvider));
        modify.accept(item);
        pool.add(item);
    }
    //?}

}