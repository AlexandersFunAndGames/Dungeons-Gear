package com.infamous.dungeons_gear.enchantments.melee;

import com.infamous.dungeons_gear.config.DungeonsGearConfig;
import com.infamous.dungeons_gear.enchantments.ModEnchantmentTypes;
import com.infamous.dungeons_gear.enchantments.lists.MeleeEnchantmentList;
import com.infamous.dungeons_gear.enchantments.types.DropsEnchantment;
import com.infamous.dungeons_gear.utilties.LootTableHelper;
import com.infamous.dungeons_gear.utilties.ModEnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.infamous.dungeons_gear.DungeonsGear.MODID;

@Mod.EventBusSubscriber(modid= MODID)
public class ProspectorEnchantment extends DropsEnchantment {

    public ProspectorEnchantment() {
        super(Enchantment.Rarity.RARE, ModEnchantmentTypes.MELEE, new EquipmentSlotType[]{
                EquipmentSlotType.MAINHAND});
    }

    public int getMaxLevel() {
        return 3;
    }

    @SubscribeEvent
    public static void onProspectiveKill(LivingDropsEvent event){
        if(event.getSource().getDirectEntity() instanceof AbstractArrowEntity) return;
        if(event.getSource().getEntity() instanceof LivingEntity){
            LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
            ItemStack mainhand = attacker.getMainHandItem();
            LivingEntity victim = event.getEntityLiving();
            if(ModEnchantmentHelper.hasEnchantment(mainhand, MeleeEnchantmentList.PROSPECTOR)){
                int prospectorLevel = EnchantmentHelper.getItemEnchantmentLevel(MeleeEnchantmentList.PROSPECTOR, mainhand);
                float prospectorChance;
                prospectorChance = (float) (DungeonsGearConfig.PROSPECTOR_CHANCE_PER_LEVEL.get() * prospectorLevel);
                float prospectorRand = attacker.getRandom().nextFloat();
                if(prospectorRand <= prospectorChance){
                    if(victim instanceof MonsterEntity){
                        ItemEntity drop = getProspectorDrop(attacker, victim);
                        event.getDrops().add(drop);
                    }
                }
            }
        }
    }

    private static ItemEntity getProspectorDrop(LivingEntity attacker, LivingEntity victim) {
        ResourceLocation prospectorLootTable = getProspectorLootTable(victim.getCommandSenderWorld());
        ItemStack itemStack = LootTableHelper.generateItemStack((ServerWorld) victim.level, victim.blockPosition(), prospectorLootTable, attacker.getRandom());
        return new ItemEntity(victim.level, victim.getX(), victim.getY(), victim.getZ(), itemStack);
    }

    private static ResourceLocation getProspectorLootTable(World world) {
        ResourceLocation resourceLocation = new ResourceLocation(MODID, "enchantments/prospector/" + world.dimension().location().getPath());
        if(LootTableHelper.lootTableExists((ServerWorld) world, resourceLocation)){
            return resourceLocation;
        }else{
            return new ResourceLocation(MODID, "enchantments/prospector/overworld");
        }
    }

}
