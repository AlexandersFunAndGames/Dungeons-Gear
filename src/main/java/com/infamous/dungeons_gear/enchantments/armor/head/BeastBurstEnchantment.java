package com.infamous.dungeons_gear.enchantments.armor.head;

import com.infamous.dungeons_gear.DungeonsGear;
import com.infamous.dungeons_gear.config.DungeonsGearConfig;
import com.infamous.dungeons_gear.enchantments.lists.ArmorEnchantmentList;
import com.infamous.dungeons_gear.enchantments.types.BeastEnchantment;
import com.infamous.dungeons_gear.utilties.AOECloudHelper;
import com.infamous.dungeons_gear.utilties.AreaOfEffectHelper;
import com.infamous.dungeons_gear.utilties.SoundHelper;
import com.infamous.dungeons_libraries.capabilities.minionmaster.IMaster;
import com.infamous.dungeons_libraries.capabilities.minionmaster.MinionMasterHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static com.infamous.dungeons_gear.enchantments.ModEnchantmentTypes.ARMOR_SLOT;

@Mod.EventBusSubscriber(modid = DungeonsGear.MODID)
public class BeastBurstEnchantment extends BeastEnchantment {
    public BeastBurstEnchantment() {
        super(Rarity.RARE, EnchantmentType.ARMOR_HEAD, ARMOR_SLOT);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @SubscribeEvent
    public static void onPlayerUsedHealthPotion(LivingEntityUseItemEvent.Finish event){
        if(!(event.getEntityLiving() instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) event.getEntityLiving();
        if(player.isAlive() && player.level instanceof ServerWorld){
            ServerWorld serverWorld = (ServerWorld) player.level;
            List<EffectInstance> potionEffects = PotionUtils.getMobEffects(event.getItem());
            if(potionEffects.isEmpty()) return;
            if(potionEffects.get(0).getEffect() == Effects.HEAL){
                int beastBurstLevel = EnchantmentHelper.getEnchantmentLevel(ArmorEnchantmentList.BEAST_BURST, player);
                if(beastBurstLevel > 0){
                    IMaster summonerCap = MinionMasterHelper.getMasterCapability(player);
                    if(summonerCap == null) return;

                    for(Entity summonedMob : summonerCap.getSummonedMobs()){
                        if(summonedMob instanceof LivingEntity){
                            LivingEntity summonedMobAsLiving = (LivingEntity) summonedMob;
                            SoundHelper.playGenericExplodeSound(summonedMobAsLiving);
                            AOECloudHelper.spawnExplosionCloud(summonedMobAsLiving, summonedMobAsLiving, 3.0F);
                            AreaOfEffectHelper.causeExplosionAttack(summonedMobAsLiving, summonedMobAsLiving, DungeonsGearConfig.BEAST_BURST_DAMAGE_PER_LEVEL.get() * beastBurstLevel, 3.0F);
                        }
                    }
                }
            }
        }
    }

}
