package me.gravityio.multiline_mastery.enchants;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.tag.ItemTags;

public class SeafarersFortuneEnchant extends Enchantment {
    public static final int MAX_LEVEL = 3;

    public SeafarersFortuneEnchant() {
        super(Enchantment.properties(ItemTags.FISHING_ENCHANTABLE, 1, MAX_LEVEL, Enchantment.constantCost(0), Enchantment.constantCost(0), 4, EquipmentSlot.MAINHAND));
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return false;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return false;
    }
}
