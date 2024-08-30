package me.gravityio.multiline_mastery;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import me.gravityio.multiline_mastery.mixins.inter.ModPlayer;
import me.gravityio.multiline_mastery.network.SyncPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetEnchantmentsLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultilineMasteryMod implements ModInitializer, PreLaunchEntrypoint {
    public static final String MOD_ID = "multilinemastery";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean IS_DEBUG = false;

    public static final RegistryKey<Enchantment> MULTILINE_MASTERY_KEY = RegistryKey.of(RegistryKeys.ENCHANTMENT, id("multiline_mastery"));
    public static final RegistryKey<Enchantment> SEAFARERS_FORTUNE_KEY = RegistryKey.of(RegistryKeys.ENCHANTMENT, id("seafarers_fortune"));

    public static Identifier id(String str) {
        return Identifier.of(MOD_ID, str);
    }

    public static void DEBUG(String message, Object... args) {
        if (IS_DEBUG) LOGGER.info(message, args);
    }

    @Override
    public void onPreLaunch() {
        MixinExtrasBootstrap.init();
    }

    @Override
    public void onInitialize() {
        IS_DEBUG = FabricLoader.getInstance().isDevelopmentEnvironment();

        ModConfig.HANDLER.load();

        PayloadTypeRegistry.playC2S().register(SyncPacket.ID, SyncPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SyncPacket.ID, (payload, context) -> {
            DEBUG("Setting angle for player '{}' to {}", context.player().getName().getString(), payload.getAngle());
            ModPlayer modPlayer = (ModPlayer) context.player();
            modPlayer.fishingButGood$setAngle(payload.getAngle());
        });

        LootTableEvents.MODIFY.register(this::onInitLoot);
    }

    private void onInitLoot(RegistryKey<LootTable> key, LootTable.Builder builder, LootTableSource lootTableSource, RegistryWrapper.WrapperLookup registries) {
        if (!LootTables.FISHING_TREASURE_GAMEPLAY.equals(key) && !EntityType.ELDER_GUARDIAN.getLootTableId().equals(key)) return;
        var lookup = registries.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        var multiline = lookup.getOrThrow(MULTILINE_MASTERY_KEY);
        var seafarer = lookup.getOrThrow(SEAFARERS_FORTUNE_KEY);

        if (LootTables.FISHING_TREASURE_GAMEPLAY.equals(key)) {
            DEBUG("Registering FISHING_TREASURE_GAMEPLAY Loot Table");
            var pool = LootPool.builder();
            pool.conditionally(RandomChanceLootCondition.builder(0.1f).build());
            var lowFarer = getEnchantBookEntry(seafarer, ConstantLootNumberProvider.create(1)).weight(40); // 0.57
            var lowMulti = getEnchantBookEntry(multiline, ConstantLootNumberProvider.create(1)).weight(20); // 0.28
            var highFarer = getEnchantBookEntry(seafarer, UniformLootNumberProvider.create(2, 3)).weight(6); // 0.08
            var highMulti = getEnchantBookEntry(multiline, UniformLootNumberProvider.create(2, 3)).weight(3); // 0.04
            pool.with(lowFarer);
            pool.with(lowMulti);
            pool.with(highFarer);
            pool.with(highMulti);
            builder.pool(pool);
        } else if (EntityType.ELDER_GUARDIAN.getLootTableId().equals(key)) {
            DEBUG("Registering ELDER_GUARDIAN Loot Table");
            var pool = LootPool.builder();
            pool.conditionally(RandomChanceLootCondition.builder(0.25f).build());
            var lowFishes = getEnchantBookEntry(seafarer, ConstantLootNumberProvider.create(1)).weight(2); // 0.66
            var lowMulti = getEnchantBookEntry(multiline, ConstantLootNumberProvider.create(1)).weight(1); // 0.33
            pool.with(lowFishes);
            pool.with(lowMulti);
            builder.pool(pool);
        }
    }

    private static ItemEntry.Builder<?> getEnchantBookEntry(RegistryEntry<Enchantment> enchant, LootNumberProvider numberProvider) {
        return ItemEntry.builder(Items.BOOK).apply(new SetEnchantmentsLootFunction.Builder().enchantment(enchant, numberProvider));
    }

}
