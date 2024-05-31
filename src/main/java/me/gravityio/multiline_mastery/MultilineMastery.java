package me.gravityio.multiline_mastery;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import me.gravityio.multiline_mastery.enchants.MultilineMasteryEnchant;
import me.gravityio.multiline_mastery.enchants.SeafarersFortuneEnchant;
import me.gravityio.multiline_mastery.mixins.inter.ModPlayer;
import me.gravityio.multiline_mastery.network.SyncPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultilineMastery implements ModInitializer, PreLaunchEntrypoint {
    public static String MOD_ID = "multilinemastery";
    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MultilineMasteryEnchant MULTILINE_MASTERY_ENCHANT;
    public static SeafarersFortuneEnchant SEAFARERS_FORTUNE_ENCHANT;
    public static boolean IS_DEBUG = false;

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
        MULTILINE_MASTERY_ENCHANT = new MultilineMasteryEnchant();
        SEAFARERS_FORTUNE_ENCHANT = new SeafarersFortuneEnchant();

        ModConfig.HANDLER.load();

        ServerPlayNetworking.registerGlobalReceiver(SyncPacket.TYPE, (packet, player, responseSender) -> {
            DEBUG("Setting angle for player '{}' to {}", player.getName().getString(), packet.getAngle());
            ModPlayer modPlayer = (ModPlayer) player;
            modPlayer.fishingButGood$setAngle(packet.getAngle());
        });

        Registry.register(Registries.ENCHANTMENT, new Identifier(MOD_ID, "multiline_mastery"), MULTILINE_MASTERY_ENCHANT);
        Registry.register(Registries.ENCHANTMENT, new Identifier(MOD_ID, "seafarers_fortune"), SEAFARERS_FORTUNE_ENCHANT);
        LootTableEvents.MODIFY.register(this::onInitLoot);
    }

    private void onInitLoot(ResourceManager resourceManager, LootManager lootManager, Identifier identifier, LootTable.Builder builder, LootTableSource lootTableSource) {
        if (LootTables.FISHING_TREASURE_GAMEPLAY.equals(identifier)) {
            DEBUG("Registering FISHING_TREASURE_GAMEPLAY Loot Table");
            var pool = LootPool.builder();
            pool.conditionally(RandomChanceLootCondition.builder(0.1f).build());
            var lowFarer = getEnchantBookEntry(SEAFARERS_FORTUNE_ENCHANT, ConstantLootNumberProvider.create(1)).weight(40); // 0.57
            var lowMulti = getEnchantBookEntry(MULTILINE_MASTERY_ENCHANT, ConstantLootNumberProvider.create(1)).weight(20); // 0.28
            var highFarer = getEnchantBookEntry(SEAFARERS_FORTUNE_ENCHANT, UniformLootNumberProvider.create(2, 3)).weight(6); // 0.08
            var highMulti = getEnchantBookEntry(MULTILINE_MASTERY_ENCHANT, UniformLootNumberProvider.create(2, 3)).weight(3); // 0.04
            pool.with(lowFarer);
            pool.with(lowMulti);
            pool.with(highFarer);
            pool.with(highMulti);
            builder.pool(pool);
        } else if (EntityType.ELDER_GUARDIAN.getLootTableId().equals(identifier)) {
            DEBUG("Registering ELDER_GUARDIAN Loot Table");
            var pool = LootPool.builder();
            pool.conditionally(RandomChanceLootCondition.builder(0.25f).build());
            var lowFishes = getEnchantBookEntry(SEAFARERS_FORTUNE_ENCHANT, ConstantLootNumberProvider.create(1)).weight(2); // 0.66
            var lowMulti = getEnchantBookEntry(MULTILINE_MASTERY_ENCHANT, ConstantLootNumberProvider.create(1)).weight(1); // 0.33
            pool.with(lowFishes);
            pool.with(lowMulti);
            builder.pool(pool);
        }
    }

    private static ItemEntry.Builder<?> getEnchantBookEntry(Enchantment enchant, LootNumberProvider numberProvider) {
        return ItemEntry.builder(Items.BOOK).apply(new SetEnchantmentsLootFunction.Builder().enchantment(enchant, numberProvider));
    }

}
