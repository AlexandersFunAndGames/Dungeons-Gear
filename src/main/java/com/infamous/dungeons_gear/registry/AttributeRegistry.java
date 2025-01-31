package com.infamous.dungeons_gear.registry;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.infamous.dungeons_gear.DungeonsGear.MODID;

public class AttributeRegistry {

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MODID);

    public static final RegistryObject<Attribute> ATTACK_REACH = ATTRIBUTES.register("attack_reach", () -> new RangedAttribute(
            "attribute.name.generic.dungeons_gear.attackReach",
            3.0D,
            0.0D,
            1024.0D)
            .setSyncable(true));

    public static final RegistryObject<Attribute> ROLL_COOLDOWN = ATTRIBUTES.register("roll_cooldown", () -> new RangedAttribute(
            "attribute.name.generic.dungeons_gear.roll_cooldown",
            20.0D,
            0.0D,
            1024.0D)
            .setSyncable(true));

    public static final RegistryObject<Attribute> ROLL_LIMIT = ATTRIBUTES.register("roll_limit", () -> new RangedAttribute(
            "attribute.name.generic.dungeons_gear.roll_cooldown",
            1.0D,
            0.0D,
            1024.0D)
            .setSyncable(true));
}
