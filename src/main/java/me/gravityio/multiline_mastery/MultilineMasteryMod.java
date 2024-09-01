package me.gravityio.multiline_mastery;

import me.gravityio.multiline_mastery.mixins.inter.ModPlayer;
import me.gravityio.multiline_mastery.network.SyncPacket;
import me.gravityio.multiline_mastery.versioned.Common;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.world.item.enchantment.Enchantment;

//? if >=1.20.5 {
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.resources.ResourceKey;
//?}

//? if <=1.20.5 {
/*import me.gravityio.multiline_mastery.versioned.v1_20_5.MultilineMasteryEnchant;
import me.gravityio.multiline_mastery.versioned.v1_20_5.SeafarersFortuneEnchant;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
*///?}

//? if >=1.21 {
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
//?} elif >=1.20.5 {

//?} else {
/*import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootDataManager;
*///?}

public class MultilineMasteryMod implements ModInitializer {
    public static final String MOD_ID = "multilinemastery";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean IS_DEBUG = false;

    //? if >=1.21 {
    public static final ResourceKey<Enchantment> MULTILINE_MASTERY_KEY = ResourceKey.create(Registries.ENCHANTMENT, id("multiline_mastery"));
    public static final ResourceKey<Enchantment> SEAFARERS_FORTUNE_KEY = ResourceKey.create(Registries.ENCHANTMENT, id("seafarers_fortune"));
    //?} else {
    /*public static MultilineMasteryEnchant MULTILINE_MASTERY_ENCHANT = new MultilineMasteryEnchant();
    public static SeafarersFortuneEnchant SEAFARERS_FORTUNE_ENCHANT = new SeafarersFortuneEnchant();
    *///?}

    public static ResourceLocation id(String str) {
        return Common.parse(MOD_ID, str);
    }

    public static void DEBUG(String message, Object... args) {
        if (IS_DEBUG) LOGGER.info(message, args);
    }

    @Override
    public void onInitialize() {
        IS_DEBUG = FabricLoader.getInstance().isDevelopmentEnvironment();

        ModConfig.HANDLER.load();

        //? if <=1.20.5 {
        /*Registry.register(BuiltInRegistries.ENCHANTMENT, id("multiline_mastery"), MULTILINE_MASTERY_ENCHANT);
        Registry.register(BuiltInRegistries.ENCHANTMENT, id("seafarers_fortune"), SEAFARERS_FORTUNE_ENCHANT);
        *///?}

        //? if >=1.20.5 {
        PayloadTypeRegistry.playC2S().register(SyncPacket.TYPE, SyncPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SyncPacket.TYPE, (payload, context) -> {
            DEBUG("Setting angle for player '{}' to {}", context.player().getName().getString(), payload.getAngle());
            ModPlayer modPlayer = (ModPlayer) context.player();
            modPlayer.fishingButGood$setAngle(payload.getAngle());
        });
        //?} else {
        /*ServerPlayNetworking.registerGlobalReceiver(SyncPacket.TYPE, (packet, player, responseSender) -> {
            DEBUG("Setting angle for player '{}' to {}", player.getName().getString(), packet.getAngle());
            ModPlayer modPlayer = (ModPlayer) player;
            modPlayer.fishingButGood$setAngle(packet.getAngle());
        });
        *///?}

        LootTableEvents.MODIFY.register(this::onInitLoot);
    }

    //? if >=1.21 {
    private void onInitLoot(ResourceKey<LootTable> key, LootTable.Builder builder, LootTableSource lootTableSource, HolderLookup.Provider registries) {
    //?} elif >=1.20.5 {
    /*private void onInitLoot(ResourceKey<LootTable> key, LootTable.Builder builder, LootTableSource source) {
    *///?} else {
    /*private void onInitLoot(ResourceManager resourceManager, LootDataManager lootDataManager, ResourceLocation key, LootTable.Builder builder, LootTableSource source) {
    *///?}
        if (!BuiltInLootTables.FISHING_TREASURE.equals(key) && !EntityType.ELDER_GUARDIAN.getDefaultLootTable().equals(key)) return;

        if (BuiltInLootTables.FISHING_TREASURE.equals(key)) {
            DEBUG("Registering FISHING_TREASURE_GAMEPLAY Loot Table");
            var pool = LootPool.lootPool();
            pool.conditionally(LootItemRandomChanceCondition.randomChance(0.1f).build());
            //? if >=1.21 {
            Common.addToLootable(pool, registries, SEAFARERS_FORTUNE_KEY, ConstantValue.exactly(1), (item) -> item.setWeight(40));
            Common.addToLootable(pool, registries, MULTILINE_MASTERY_KEY, ConstantValue.exactly(1), (item) -> item.setWeight(20));
            Common.addToLootable(pool, registries, SEAFARERS_FORTUNE_KEY, UniformGenerator.between(2, 3), (item) -> item.setWeight(6));
            Common.addToLootable(pool, registries, MULTILINE_MASTERY_KEY, UniformGenerator.between(2, 3), (item) -> item.setWeight(3));
            //?} else {
            /*Common.addToLootable(pool, SEAFARERS_FORTUNE_ENCHANT, ConstantValue.exactly(1), (item) -> item.setWeight(40));
            Common.addToLootable(pool, MULTILINE_MASTERY_ENCHANT, ConstantValue.exactly(1), (item) -> item.setWeight(20));
            Common.addToLootable(pool, SEAFARERS_FORTUNE_ENCHANT, UniformGenerator.between(2, 3), (item) -> item.setWeight(6));
            Common.addToLootable(pool, MULTILINE_MASTERY_ENCHANT, UniformGenerator.between(2, 3), (item) -> item.setWeight(3));
            *///?}
            builder.withPool(pool);
        } else if (EntityType.ELDER_GUARDIAN.getDefaultLootTable().equals(key)) {
            DEBUG("Registering ELDER_GUARDIAN Loot Table");
            var pool = LootPool.lootPool();
            pool.conditionally(LootItemRandomChanceCondition.randomChance(0.25f).build());
            //? if >=1.21 {
            Common.addToLootable(pool, registries, SEAFARERS_FORTUNE_KEY, ConstantValue.exactly(1), item -> item.setWeight(2));
            Common.addToLootable(pool, registries, MULTILINE_MASTERY_KEY, ConstantValue.exactly(1), item -> item.setWeight(1));
            //?} else {
            /*Common.addToLootable(pool, SEAFARERS_FORTUNE_ENCHANT, ConstantValue.exactly(1), item -> item.setWeight(2));
            Common.addToLootable(pool, MULTILINE_MASTERY_ENCHANT, ConstantValue.exactly(1), item -> item.setWeight(1));
            *///?}
            builder.withPool(pool);
        }
    }

    //? if >=1.21 {
    private static LootPoolSingletonContainer.Builder<?> getEnchantBookEntry(Holder<Enchantment> enchant, NumberProvider numberProvider) {
        return LootItem.lootTableItem(Items.BOOK).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchant, numberProvider));
    }
    //?} else {
    /*private static LootPoolSingletonContainer.Builder<?> getEnchantBookEntry(Enchantment enchant, NumberProvider numberProvider) {
        return LootItem.lootTableItem(Items.BOOK).apply(new SetEnchantmentsFunction.Builder().withEnchantment(enchant, numberProvider));
    }
    *///?}

}
