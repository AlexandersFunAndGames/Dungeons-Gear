package com.infamous.dungeons_gear.loot;

import com.infamous.dungeons_gear.DungeonsGear;
import com.infamous.dungeons_gear.config.DungeonsGearConfig;
import com.infamous.dungeons_gear.utilties.SoundHelper;
import com.infamous.dungeons_libraries.items.materials.armor.ArmorMaterialBaseType;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.IReputationType;
import net.minecraft.entity.merchant.villager.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.common.BasicTrade;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.Predicate;

import static com.infamous.dungeons_gear.items.armor.ArmorHelper.getArmorList;
import static com.infamous.dungeons_gear.items.artifacts.ArtifactHelper.getArtifactList;
import static com.infamous.dungeons_gear.items.melee.MeleeWeaponHelper.getMeleeWeaponList;
import static com.infamous.dungeons_gear.items.ranged.RangedWeaponHelper.getRangedWeaponList;
import static com.infamous.dungeons_gear.registry.ItemRegistry.SHEAR_DAGGER;
import static com.infamous.dungeons_libraries.items.materials.armor.ArmorMaterialBaseType.BONE;
import static com.infamous.dungeons_libraries.items.materials.armor.ArmorMaterialBaseType.LEATHER;
import static com.infamous.dungeons_libraries.items.materials.armor.ArmorMaterialBaseType.*;
import static net.minecraft.item.Items.*;


@Mod.EventBusSubscriber(modid = DungeonsGear.MODID)
public class TradeEvents {

    private static List<ArmorMaterialBaseType> METAL_MATERIALS = Arrays.asList(METAL, GEM);
    private static List<ArmorMaterialBaseType> LEATHER_MATERIALS = Arrays.asList(CLOTH, BONE, LEATHER);

    @SubscribeEvent
    public static void onWandererTrades(WandererTradesEvent event){
        if(!DungeonsGearConfig.ENABLE_VILLAGER_TRADES.get()) return;
        if(!ConfigurableLootHelper.isArtifactLootEnabled()) return;

        List<VillagerTrades.ITrade> genericTrades = event.getGenericTrades();
        List<VillagerTrades.ITrade> rareTrades = event.getRareTrades();

//        moveTradesToDifferentGroup(rareTrades, genericTrades);

        for(Item item : getArtifactList()){
            Item artifact = ForgeRegistries.ITEMS.getValue(item.getRegistryName());
            ItemStack artifactStack = new ItemStack(artifact);
            BasicTrade trade = new BasicTrade(DungeonsGearConfig.ARTIFACT_VALUE.get(), artifactStack, 3, 30);
            rareTrades.add(trade);
        }
    }

    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event){
        if(!DungeonsGearConfig.ENABLE_VILLAGER_TRADES.get()) return;

        if(event.getType() == VillagerProfession.WEAPONSMITH){
            if(!ConfigurableLootHelper.isMeleeWeaponLootEnabled()) return;

            Int2ObjectMap<List<VillagerTrades.ITrade>> weaponsmithTrades = event.getTrades();

            // Moving the Level 4 "Diamond for Emerald" Trade down to Level 3
            moveTrade(weaponsmithTrades, 4, 3, emeraldForItemFilter(DIAMOND));

            List<Item> commonList = getMeleeWeaponList(false);
            List<Item> uniqueList = getMeleeWeaponList(true);

            addCommonAndUniqueTradesNew(weaponsmithTrades, commonList, uniqueList);
        }
        if(event.getType() == VillagerProfession.FLETCHER){
            if(!ConfigurableLootHelper.isRangedWeaponLootEnabled()) return;

            Int2ObjectMap<List<VillagerTrades.ITrade>> fletcherTrades = event.getTrades();

            // Move higher level stuff to lower trade levels
            moveTrade(fletcherTrades, 4, 2, emeraldForItemFilter(FEATHER));
            moveTrade(fletcherTrades, 5, 3, emeraldForItemFilter(TRIPWIRE_HOOK));
            moveTrade(fletcherTrades, 5, 3, itemWithPotionForEmeraldsAndItemsFilter(TIPPED_ARROW));

            //moveTradesToDifferentGroup(fletcherTrades.get(4), fletcherTrades.get(2));
            //moveTradesToDifferentGroup(fletcherTrades.get(5),  fletcherTrades.get(3));
            List<Item> commonList = getRangedWeaponList(false);
            List<Item> uniqueList = getRangedWeaponList(true);

            addCommonAndUniqueTradesNew(fletcherTrades, commonList, uniqueList);
        }

        if(event.getType() == VillagerProfession.ARMORER){
            if(!ConfigurableLootHelper.isArmorLootEnabled()) return;

            Int2ObjectMap<List<VillagerTrades.ITrade>> armorerTrades = event.getTrades();
            List<Item> commonList = getArmorList(METAL_MATERIALS, false);
            List<Item> uniqueList = getArmorList(METAL_MATERIALS, true);

            addCommonAndUniqueTradesNew(armorerTrades, commonList, uniqueList);
        }

        if(event.getType() == VillagerProfession.LEATHERWORKER){
            if(!ConfigurableLootHelper.isArmorLootEnabled()) return;

            Int2ObjectMap<List<VillagerTrades.ITrade>> leatherworkerTrades = event.getTrades();

            // Move higher level stuff to lower trade levels
            moveTradesToDifferentGroup(leatherworkerTrades.get(4), leatherworkerTrades.get(2));
            moveTradesToDifferentGroup(leatherworkerTrades.get(5),  leatherworkerTrades.get(3));
            List<Item> commonList = getArmorList(LEATHER_MATERIALS, false);
            List<Item> uniqueList = getArmorList(LEATHER_MATERIALS, true);

            addCommonAndUniqueTradesNew(leatherworkerTrades, commonList, uniqueList);
        }
        if(event.getType() == VillagerProfession.SHEPHERD){
            if(!ConfigurableLootHelper.isMeleeWeaponLootEnabled()) return;

            Int2ObjectMap<List<VillagerTrades.ITrade>> shepherdTrades = event.getTrades();

            TradeHelper.EnchantedItemForEmeraldsTrade trade = new TradeHelper.EnchantedItemForEmeraldsTrade(SHEAR_DAGGER.get(), DungeonsGearConfig.UNIQUE_ITEM_VALUE.get(), 3, 30, 0.2F);
            shepherdTrades.get(5).add(trade);
        }
    }

    private static void moveTrade(Int2ObjectMap<List<VillagerTrades.ITrade>> trades, int source, int target, Predicate<VillagerTrades.ITrade> filter){
        Optional<VillagerTrades.ITrade> emeraldForFeatherTrade = getVillagerTrade(trades.get(source), filter);
        emeraldForFeatherTrade.ifPresent(iTrade -> {
            trades.get(target).add(iTrade);
            trades.get(source).removeIf(filter);
        });
    }

    private static Optional<VillagerTrades.ITrade> getVillagerTrade(List<VillagerTrades.ITrade> trades, Predicate< VillagerTrades.ITrade > filter){
        return trades.stream().filter(filter).findFirst();
    }

    private static Predicate<VillagerTrades.ITrade> emeraldForItemFilter(Item item){
        return (iTrade) -> (iTrade instanceof VillagerTrades.EmeraldForItemsTrade && ((VillagerTrades.EmeraldForItemsTrade) iTrade).item.equals(item));
    }

    private static Predicate<VillagerTrades.ITrade> itemsForEmeraldsFilter(Item item){
        return (iTrade) -> (iTrade instanceof VillagerTrades.ItemsForEmeraldsTrade && ((VillagerTrades.ItemsForEmeraldsTrade) iTrade).itemStack.getItem().equals(item));
    }

    private static Predicate<VillagerTrades.ITrade> itemWithPotionForEmeraldsAndItemsFilter(Item item){
        return (iTrade) -> (iTrade instanceof VillagerTrades.ItemWithPotionForEmeraldsAndItemsTrade && ((VillagerTrades.ItemWithPotionForEmeraldsAndItemsTrade) iTrade).toItem.getItem().equals(item));
    }

    private static void addCommonAndUniqueTradesNew(Int2ObjectMap<List<VillagerTrades.ITrade>> villagerTrades, List<Item> commonList, List<Item> uniqueList) {
        for (Item item1 : commonList) {
            TradeHelper.EnchantedItemForEmeraldsTrade enchantedItemForEmeraldsTrade = new TradeHelper.EnchantedItemForEmeraldsTrade(item1, DungeonsGearConfig.COMMON_ITEM_VALUE.get(), 3, 15, 0.2F);
            villagerTrades.get(4).add(enchantedItemForEmeraldsTrade);
        }
        for (Item item : uniqueList) {
            TradeHelper.EnchantedItemForEmeraldsTrade trade = new TradeHelper.EnchantedItemForEmeraldsTrade(item, DungeonsGearConfig.UNIQUE_ITEM_VALUE.get(), 3, 30, 0.2F);
            villagerTrades.get(5).add(trade);
        }
    }

    private static void addCommonAndUniqueTrades(Int2ObjectMap<List<VillagerTrades.ITrade>> villagerTrades, Map<Item, ResourceLocation> commonMap, Map<Item, ResourceLocation> uniqueMap) {
        for(Item item : commonMap.keySet()){
            ResourceLocation resourceLocation = commonMap.get(item);
            Item weapon = ForgeRegistries.ITEMS.getValue(resourceLocation);
            ItemStack weaponStack = new ItemStack(weapon);
            TradeHelper.EnchantedItemForEmeraldsTrade trade = new TradeHelper.EnchantedItemForEmeraldsTrade(weapon, DungeonsGearConfig.COMMON_ITEM_VALUE.get(), 3, 15,0.2F);

            villagerTrades.get(4).add(trade);
        }
        for(Item item : uniqueMap.keySet()){
            ResourceLocation resourceLocation = uniqueMap.get(item);
            Item weapon = ForgeRegistries.ITEMS.getValue(resourceLocation);
            ItemStack weaponStack = new ItemStack(weapon);
            TradeHelper.EnchantedItemForEmeraldsTrade trade = new TradeHelper.EnchantedItemForEmeraldsTrade(weapon, DungeonsGearConfig.UNIQUE_ITEM_VALUE.get(), 3, 30,0.2F);

            villagerTrades.get(5).add(trade);
        }
    }

    private static void moveTradesToDifferentGroup(List<VillagerTrades.ITrade> oldTradesGroup, List<VillagerTrades.ITrade> newTradesGroup) {
        Iterator<VillagerTrades.ITrade> it = oldTradesGroup.iterator();
        while (it.hasNext()) {
            VillagerTrades.ITrade currentTrade = it.next();
            if (currentTrade != null) {
                newTradesGroup.add(currentTrade);
                it.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onSalvageItem(PlayerInteractEvent.EntityInteract event){
        if(!DungeonsGearConfig.ENABLE_SALVAGING.get()) return;
        Entity entity = event.getTarget();
        PlayerEntity playerEntity = event.getPlayer();
        if(entity instanceof VillagerEntity){
            VillagerEntity villagerEntity = (VillagerEntity) entity;
            if(villagerEntity.getVillagerData().getProfession() == VillagerProfession.WEAPONSMITH){
                if(playerEntity.isShiftKeyDown()){
                    ItemStack interactStack = playerEntity.getItemInHand(event.getHand());
                    if(getMeleeWeaponList(false).contains(interactStack.getItem())){
                        handleSalvageTrade(playerEntity, villagerEntity, interactStack, "COMMON");
                    }
                    else if(getMeleeWeaponList(true).contains(interactStack.getItem())){
                        handleSalvageTrade(playerEntity, villagerEntity, interactStack, "UNIQUE");
                    }
                }
            }
            if(villagerEntity.getVillagerData().getProfession() == VillagerProfession.FLETCHER){
                if(playerEntity.isShiftKeyDown()){
                    ItemStack interactStack = playerEntity.getItemInHand(event.getHand());
                    if(getRangedWeaponList(false).contains(interactStack.getItem())){
                        handleSalvageTrade(playerEntity, villagerEntity, interactStack, "COMMON");
                    }
                    else if(getRangedWeaponList(true).contains(interactStack.getItem())){
                        handleSalvageTrade(playerEntity, villagerEntity, interactStack, "UNIQUE");
                    }
                }
            }
            if(villagerEntity.getVillagerData().getProfession() == VillagerProfession.ARMORER){
                if(playerEntity.isShiftKeyDown()){
                    ItemStack interactStack = playerEntity.getItemInHand(event.getHand());
                    if(getArmorList(METAL_MATERIALS, false).contains(interactStack.getItem())){
                        handleSalvageTrade(playerEntity, villagerEntity, interactStack, "COMMON");
                    }
                    else if(getArmorList(METAL_MATERIALS, true).contains(interactStack.getItem())){
                        handleSalvageTrade(playerEntity, villagerEntity, interactStack, "UNIQUE");
                    }
                }
            }
            if(villagerEntity.getVillagerData().getProfession() == VillagerProfession.LEATHERWORKER){
                if(playerEntity.isShiftKeyDown()){
                    ItemStack interactStack = playerEntity.getItemInHand(event.getHand());
                    if(getArmorList(LEATHER_MATERIALS, false).contains(interactStack.getItem())){
                        handleSalvageTrade(playerEntity, villagerEntity, interactStack, "COMMON");
                    }
                    else if(getArmorList(LEATHER_MATERIALS, true).contains(interactStack.getItem())){
                        handleSalvageTrade(playerEntity, villagerEntity, interactStack, "UNIQUE");
                    }
                }
            }
        }
        if(entity instanceof WanderingTraderEntity){
            WanderingTraderEntity wanderingTraderEntity = (WanderingTraderEntity) entity;
            if(playerEntity.isShiftKeyDown()){
                ItemStack interactStack = playerEntity.getItemInHand(event.getHand());
                if(getArtifactList().contains(interactStack.getItem())){
                    handleSalvageTrade(playerEntity, wanderingTraderEntity, interactStack, "ARTIFACT");
                }
            }
        }
    }

    private static void handleSalvageTrade(PlayerEntity playerEntity, AbstractVillagerEntity abstractVillagerEntity, ItemStack interactStack, String itemType) {
        float maxDamage = interactStack.getMaxDamage();
        DungeonsGear.LOGGER.info("Max damage: " + maxDamage);
        float currentDamage = maxDamage - interactStack.getDamageValue();
        DungeonsGear.LOGGER.info("Current damage: " + currentDamage);

        int itemValue;
        switch(itemType.toUpperCase()){
            case "COMMON":
                itemValue = DungeonsGearConfig.COMMON_ITEM_VALUE.get();
                break;
            case "UNIQUE":
                itemValue  = DungeonsGearConfig.UNIQUE_ITEM_VALUE.get();
                break;
            case "ARTIFACT":
                itemValue = DungeonsGearConfig.ARTIFACT_VALUE.get();
                break;
            default:
                itemValue = DungeonsGearConfig.COMMON_ITEM_VALUE.get();
        }

        int emeraldReward = Math.round(itemValue * 0.375F);
        float durabilityModifier = currentDamage / maxDamage;
        emeraldReward = Math.round(emeraldReward * durabilityModifier);

        if(emeraldReward > 0){
            interactStack.shrink(1);

            if(abstractVillagerEntity instanceof VillagerEntity){
                VillagerEntity villagerEntity = (VillagerEntity)abstractVillagerEntity;
                SoundHelper.playCreatureSound(villagerEntity, SoundEvents.VILLAGER_YES);
                villagerEntity.onReputationEventFrom(IReputationType.TRADE, playerEntity);
            }
            else if(abstractVillagerEntity instanceof WanderingTraderEntity){
                WanderingTraderEntity wanderingTraderEntity = (WanderingTraderEntity)abstractVillagerEntity;
                SoundHelper.playCreatureSound(wanderingTraderEntity, SoundEvents.WANDERING_TRADER_YES);
            }
            ItemStack emeraldStack = new ItemStack(Items.EMERALD, emeraldReward);
            if(!playerEntity.addItem(emeraldStack)){
                World world = playerEntity.getCommandSenderWorld();
                ItemEntity emeraldStackEntity = new ItemEntity(world, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), emeraldStack);
                world.addFreshEntity(emeraldStackEntity);
            }
            playerEntity.giveExperiencePoints(emeraldReward);
        }
    }
}
