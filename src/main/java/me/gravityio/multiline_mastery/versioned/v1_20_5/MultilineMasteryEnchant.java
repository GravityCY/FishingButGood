package me.gravityio.multiline_mastery.versioned.v1_20_5;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

//? if =1.20.5 {
/*import net.minecraft.tags.ItemTags;
*///?} elif =1.20 {
import net.minecraft.world.item.enchantment.EnchantmentCategory;
//?}

//? if <=1.20.5 {
public class MultilineMasteryEnchant extends Enchantment {
    public static final int MAX_LEVEL = 3;

    public MultilineMasteryEnchant() {
        //? if =1.20.5 {
        /*super(Enchantment.definition(ItemTags.FISHING_ENCHANTABLE, 1, MAX_LEVEL, Enchantment.constantCost(0), Enchantment.constantCost(0), 4, EquipmentSlot.MAINHAND));
        *///?} else {
        super(Rarity.VERY_RARE, EnchantmentCategory.FISHING_ROD, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
        //?}
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    //? if =1.20 {
    @Override
    public int getMaxLevel() {
        return MAX_LEVEL;
    }
    //?}
}
//?}