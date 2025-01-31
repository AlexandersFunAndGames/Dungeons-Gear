package com.infamous.dungeons_gear.items.artifacts;

import com.infamous.dungeons_gear.DungeonsGear;
import com.infamous.dungeons_gear.capabilities.combo.ICombo;
import com.infamous.dungeons_gear.effects.CustomEffects;
import com.infamous.dungeons_gear.utilties.AreaOfEffectHelper;
import com.infamous.dungeons_gear.utilties.CapabilityHelper;
import com.infamous.dungeons_gear.utilties.SoundHelper;
import com.infamous.dungeons_libraries.capabilities.minionmaster.IMinion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.infamous.dungeons_libraries.capabilities.minionmaster.MinionMasterHelper.getMinionCapability;

@Mod.EventBusSubscriber(modid = DungeonsGear.MODID)
public class ArtifactEvents {

    public static final String FIRE_SHEEP_TAG = "FireSheep";
    public static final String POISON_SHEEP_TAG = "PoisonSheep";
    public static final String SPEED_SHEEP_TAG = "SpeedSheep";

    @SubscribeEvent
    public static void onEnchantedSheepAttack(LivingDamageEvent event){
        if(event.getSource().getEntity() instanceof SheepEntity){
            SheepEntity sheepEntity = (SheepEntity)event.getSource().getEntity();
            IMinion summonableCap = getMinionCapability(sheepEntity);
            if(summonableCap == null) return;
            if(summonableCap.getMaster() != null){
                if(sheepEntity.getTags().contains(FIRE_SHEEP_TAG)){
                    event.getEntityLiving().setSecondsOnFire(5);
                }
                else if(sheepEntity.getTags().contains(POISON_SHEEP_TAG)){
                    EffectInstance poison = new EffectInstance(Effects.POISON, 100);
                    event.getEntityLiving().addEffect(poison);
                }
            }
        }
    }

    @SubscribeEvent
    public static void updateBlueEnchantedSheep(LivingEvent.LivingUpdateEvent event){
        if(event.getEntityLiving() instanceof SheepEntity){
            SheepEntity sheepEntity = (SheepEntity)event.getEntityLiving();
            IMinion summonableCap = getMinionCapability(sheepEntity);
            if(summonableCap == null) return;
            LivingEntity summoner = summonableCap.getMaster();
            if(summoner != null){
                if(sheepEntity.level instanceof ServerWorld){
                    if(!summoner.hasEffect(Effects.MOVEMENT_SPEED) && sheepEntity.getTags().contains(SPEED_SHEEP_TAG)){
                        EffectInstance speed = new EffectInstance(Effects.MOVEMENT_SPEED, 100);
                        summoner.addEffect(speed);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onSoulProtection(LivingDeathEvent event){
        if(event.getEntityLiving().getEffect(CustomEffects.SOUL_PROTECTION) != null){
            event.setCanceled(true);
            event.getEntityLiving().setHealth(1.0F);
            event.getEntityLiving().removeAllEffects();
            event.getEntityLiving().addEffect(new EffectInstance(Effects.REGENERATION, 900, 1));
            event.getEntityLiving().addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 900, 1));
            event.getEntityLiving().addEffect(new EffectInstance(Effects.ABSORPTION, 100, 1));
        }
    }

    @SubscribeEvent
    public static void onArrowJoinWorld(EntityJoinWorldEvent event){
        if(event.getEntity() instanceof AbstractArrowEntity){
            AbstractArrowEntity arrowEntity = (AbstractArrowEntity) event.getEntity();
            Entity shooter = arrowEntity.getOwner();
            if(shooter instanceof PlayerEntity){
                PlayerEntity playerEntity = (PlayerEntity) shooter;
                ICombo comboCap = CapabilityHelper.getComboCapability(playerEntity);
                if(comboCap == null) return;
                if(comboCap.getFlamingArrowsCount() > 0){
                    int count = comboCap.getFlamingArrowsCount();
                    arrowEntity.setSecondsOnFire(100);
                    count--;
                    comboCap.setFlamingArrowsCount(count);
                }
                if(comboCap.getTormentArrowsCount() > 0){
                    arrowEntity.addTag(TormentQuiverItem.TORMENT_ARROW);
                    int count = comboCap.getTormentArrowsCount();
                    arrowEntity.setNoGravity(true);
                    arrowEntity.setDeltaMovement(arrowEntity.getDeltaMovement().scale(0.5D));
                    count--;
                    comboCap.setTormentArrowCount(count);
                }
                if(comboCap.getThunderingArrowsCount() > 0){
                    arrowEntity.addTag(ThunderingQuiverItem.THUNDERING_ARROW);
                    int count = comboCap.getThunderingArrowsCount();
                    count--;
                    comboCap.setThunderingArrowsCount(count);
                }
                if(comboCap.getHarpoonCount() > 0){
                    arrowEntity.addTag(HarpoonQuiverItem.HARPOON_QUIVER);
                    int count = comboCap.getHarpoonCount();
                    count--;
                    comboCap.setHarpoonCount(count);
                    arrowEntity.setDeltaMovement(arrowEntity.getDeltaMovement().scale(1.5D));
                    arrowEntity.setPierceLevel((byte) (arrowEntity.getPierceLevel() + 1));
                }
            }
        }
    }


    @SubscribeEvent
    public static void onSpecialArrowImpact(ProjectileImpactEvent.Arrow event)  {

        AbstractArrowEntity arrowEntity = event.getArrow();
        Entity shooter = arrowEntity.getOwner();

        if (!(shooter instanceof PlayerEntity))return;
        PlayerEntity player = (PlayerEntity) shooter;

        if (arrowEntity.getTags().contains(TormentQuiverItem.TORMENT_ARROW)){
            if (arrowEntity.tickCount > 1200){
                arrowEntity.remove();
                event.setCanceled(true);
            }

            if(event.getRayTraceResult() instanceof EntityRayTraceResult){
                EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult) event.getRayTraceResult();
                Entity targetEntity = entityRayTraceResult.getEntity();
                if(!(targetEntity instanceof LivingEntity)){
                    event.setCanceled(true);
                }

                int currentKnockbackStrength = arrowEntity.knockback;
                (arrowEntity).setKnockback(currentKnockbackStrength + 1);
            }

            if(event.getRayTraceResult() instanceof BlockRayTraceResult)event.setCanceled(true);
        }
        if (arrowEntity.getTags().contains(ThunderingQuiverItem.THUNDERING_ARROW)){
            if(event.getRayTraceResult() instanceof EntityRayTraceResult){
                EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult) event.getRayTraceResult();
                Entity targetEntity = entityRayTraceResult.getEntity();
                if(targetEntity instanceof LivingEntity){
                    SoundHelper.playLightningStrikeSounds(arrowEntity);
                    AreaOfEffectHelper.electrifyNearbyEnemies(arrowEntity, 5, 5, Integer.MAX_VALUE);
                }
            }
        }
    }
}
