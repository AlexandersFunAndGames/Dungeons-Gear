package com.infamous.dungeons_gear.enchantments.ranged;

import com.infamous.dungeons_gear.enchantments.ModEnchantmentTypes;
import com.infamous.dungeons_gear.enchantments.lists.RangedEnchantmentList;
import com.infamous.dungeons_gear.enchantments.types.DungeonsEnchantment;
import com.infamous.dungeons_gear.utilties.ProjectileEffectHelper;
import com.infamous.dungeons_libraries.utils.RangedAttackHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;

public class BurstBowstringEnchantment extends DungeonsEnchantment {

    public BurstBowstringEnchantment() {
        super(Rarity.RARE, ModEnchantmentTypes.RANGED, new EquipmentSlotType[]{
                EquipmentSlotType.MAINHAND});
    }

    public static void activateBurstBowString(LivingEntity jumper) {
        ItemStack mainhandStack = jumper.getMainHandItem();
        ItemStack offhandStack = jumper.getOffhandItem();
        int burstBowStringLevel = 0;
        float arrowVelocity = 0.0F;
        if (mainhandStack.getItem() instanceof BowItem || mainhandStack.getItem() instanceof CrossbowItem) {
            burstBowStringLevel = EnchantmentHelper.getItemEnchantmentLevel(RangedEnchantmentList.BURST_BOWSTRING, mainhandStack);
            arrowVelocity = RangedAttackHelper.getVanillaOrModdedCrossbowArrowVelocity(jumper, mainhandStack);
        } else if (offhandStack.getItem() instanceof BowItem || offhandStack.getItem() instanceof CrossbowItem) {
            burstBowStringLevel = EnchantmentHelper.getItemEnchantmentLevel(RangedEnchantmentList.BURST_BOWSTRING, offhandStack);
            arrowVelocity = RangedAttackHelper.getVanillaOrModdedCrossbowArrowVelocity(jumper, offhandStack);
        }

        if(burstBowStringLevel > 0){
            int arrowsToFire = burstBowStringLevel;
            ProjectileEffectHelper.fireBurstBowstringShots(jumper, 16, 0.4F, arrowVelocity, arrowsToFire);
        }
    }

    public int getMaxLevel() {
        return 3;
    }

}
