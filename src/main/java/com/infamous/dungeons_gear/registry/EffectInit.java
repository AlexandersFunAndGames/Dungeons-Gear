package com.infamous.dungeons_gear.registry;

import com.infamous.dungeons_gear.DungeonsGear;
import com.infamous.dungeons_gear.effects.CustomEffect;
import com.infamous.dungeons_gear.effects.CustomEffects;
import com.infamous.dungeons_gear.effects.PartyStarterEffect;
import com.infamous.dungeons_gear.effects.ShieldingEffect;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.infamous.dungeons_libraries.attribute.AttributeRegistry.LIFE_STEAL;

public class EffectInit {
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onEffectsRegistry(final RegistryEvent.Register<Effect> effectRegistryEvent) {
            effectRegistryEvent.getRegistry().registerAll(
                    CustomEffects.SHIELDING = new ShieldingEffect(EffectType.BENEFICIAL, 10044730).setRegistryName(makeResourceName("shielding")),
                    CustomEffects.SOUL_PROTECTION = new CustomEffect(EffectType.BENEFICIAL, 2445989).setRegistryName(makeResourceName("soul_protection")),
                    CustomEffects.STUNNED = new CustomEffect(EffectType.HARMFUL, 4738376).setRegistryName(makeResourceName("stunned")),
                    CustomEffects.PARTY_STARTER = new PartyStarterEffect(EffectType.BENEFICIAL, 0xE25822).setRegistryName(makeResourceName("party_starter")),
                    CustomEffects.DYNAMO = new CustomEffect(EffectType.BENEFICIAL, 0xFFBB2E).setRegistryName(makeResourceName("dynamo")),
                    CustomEffects.LIFE_STEAL = new CustomEffect(EffectType.BENEFICIAL, 0x660901).setRegistryName(makeResourceName("life_steal")).addAttributeModifier(LIFE_STEAL.get(), "ba815a35-c0d4-4bbd-b932-76a916d44eb9", 0.05F, AttributeModifier.Operation.ADDITION),
                    CustomEffects.BOW_CHARGE = new CustomEffect(EffectType.BENEFICIAL, 0x3571D4).setRegistryName(makeResourceName("bow_charge"))
            );
        }

        @SubscribeEvent
        public static void onPotionsRegistry(final RegistryEvent.Register<Potion> potionRegistryEvent){
            potionRegistryEvent.getRegistry().registerAll(
                    PotionList.SHORT_STRENGTH = new Potion("strength", new EffectInstance(Effects.DAMAGE_BOOST, 600)).setRegistryName(makeResourceName("short_strength")),
                    PotionList.SHORT_SWIFTNESS = new Potion("swiftness", new EffectInstance(Effects.MOVEMENT_SPEED, 400)).setRegistryName(makeResourceName("short_speed")),
                    PotionList.SHADOW_BREW = new Potion("shadow_brew", new EffectInstance(Effects.INVISIBILITY, 200)).setRegistryName(makeResourceName("shadow_brew")),
                    PotionList.OAKWOOD_BREW = new Potion("oakwood_brew", new EffectInstance(Effects.DAMAGE_RESISTANCE, 300)).setRegistryName(makeResourceName("oakwood_brew"))

            );
        }
        private static ResourceLocation makeResourceName(String name) {
            return new ResourceLocation(DungeonsGear.MODID, name);
        }
    }
}
