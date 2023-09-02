package me.gravityio.fishingbutgood;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetEnchantmentsLootFunction;
import net.minecraft.loot.provider.number.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FishingButGood implements ModInitializer, PreLaunchEntrypoint {
    public static String MOD_ID = "fishing_but_good";
    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final MultilineMasteryEnchant MULTILINE_MASTERY_ENCHANT = new MultilineMasteryEnchant();
    public static final SeafarersFortuneEnchant SEAFARERS_FORTUNE_ENCHANT = new SeafarersFortuneEnchant();
    public static boolean DEBUG = false;

    public static void DEBUG(String message, Object... args) {
        if (DEBUG) LOGGER.info(message, args);
    }

    @Override
    public void onPreLaunch() {
        MixinExtrasBootstrap.init();
    }

    @Override
    public void onInitialize() {
        DEBUG = FabricLoader.getInstance().isDevelopmentEnvironment();
        Registry.register(Registries.ENCHANTMENT, new Identifier("fishing_but_good", "multiline_mastery"), MULTILINE_MASTERY_ENCHANT);
        Registry.register(Registries.ENCHANTMENT, new Identifier("fishing_but_good", "seafarers_fortune"), SEAFARERS_FORTUNE_ENCHANT);
        LootTableEvents.MODIFY.register(this::onInitLoot);
    }

    private void onInitLoot(ResourceManager resourceManager, LootManager lootManager, Identifier identifier, LootTable.Builder builder, LootTableSource lootTableSource) {
        if (LootTables.FISHING_TREASURE_GAMEPLAY.equals(identifier)) {
            DEBUG("Registering FISHING_TREASURE_GAMEPLAY Loot Table");
            var pool = LootPool.builder();
            pool.conditionally(RandomChanceLootCondition.builder(0.03f).build());
            var lowFarer = getEnchantBookEntry(SEAFARERS_FORTUNE_ENCHANT, ConstantLootNumberProvider.create(1)).weight(40);
            var lowMulti = getEnchantBookEntry(MULTILINE_MASTERY_ENCHANT, ConstantLootNumberProvider.create(1)).weight(20);
            var highFarer = getEnchantBookEntry(SEAFARERS_FORTUNE_ENCHANT, UniformLootNumberProvider.create(2, 3)).weight(6);
            var highMulti = getEnchantBookEntry(MULTILINE_MASTERY_ENCHANT, UniformLootNumberProvider.create(2, 3)).weight(3);
            pool.with(lowFarer);
            pool.with(lowMulti);
            pool.with(highFarer);
            pool.with(highMulti);
            builder.pool(pool);
        } else if (EntityType.ELDER_GUARDIAN.getLootTableId().equals(identifier)) {
            DEBUG("Registering ELDER_GUARDIAN Loot Table");
            var pool = LootPool.builder();
            pool.conditionally(RandomChanceLootCondition.builder(0.1f).build());
            var lowFishes = getEnchantBookEntry(SEAFARERS_FORTUNE_ENCHANT, ConstantLootNumberProvider.create(1)).weight(2);
            var lowMulti = getEnchantBookEntry(MULTILINE_MASTERY_ENCHANT, ConstantLootNumberProvider.create(1)).weight(1);
            pool.with(lowFishes);
            pool.with(lowMulti);
            builder.pool(pool);
        }
    }

    private static ItemEntry.Builder<?> getEnchantBookEntry(Enchantment enchant, LootNumberProvider numberProvider) {
        return ItemEntry.builder(Items.BOOK).apply(new SetEnchantmentsLootFunction.Builder().enchantment(enchant, numberProvider));
    }

}
