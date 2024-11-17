package com.ffsupver.asplor.villager;

import com.simibubi.create.AllItems;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;

public class ModTraders {
    public static void registerTraders(){
        registerTrader(ModVillagers.ASSEMBLER,1,
                Items.ANDESITE,8,24,
                new ItemStack(Items.EMERALD,1),
                16,1,0.05f);
        registerTrader(ModVillagers.ASSEMBLER,1,
                Items.EMERALD,2,8,
                new ItemStack(Items.IRON_NUGGET,16),
                16,1,0.05f);
        registerTrader(ModVillagers.ASSEMBLER,2,
                AllItems.ANDESITE_ALLOY.asItem(),16,24,
                new ItemStack(Items.EMERALD,2),
                16,2,0.05f);
        registerTrader(ModVillagers.ASSEMBLER,2,
                Items.EMERALD,8,24,
                AllItems.GOGGLES.asStack(),
                1,6,0.05f);
        registerTrader(ModVillagers.ASSEMBLER,3,
                Items.COPPER_INGOT,4,8,
                new ItemStack(Items.EMERALD,1),
                16,4,0.05f);
        registerTrader(ModVillagers.ASSEMBLER,3,
                Items.EMERALD,3,5,
                AllItems.SUPER_GLUE.asStack(),
                1,8,0.05f);
        registerTrader(ModVillagers.ASSEMBLER,4,
                Items.EMERALD, 4,8,
                new ItemStack(AllItems.ZINC_INGOT.asItem(),4),
                16,8,0.05f);
        registerTrader(ModVillagers.ASSEMBLER,4,
                Items.EMERALD,33,64,
                    AllItems.BRASS_HAND.asStack(),
                1,8,0.05f);
        registerTrader(ModVillagers.ASSEMBLER,5,
                AllItems.BRASS_INGOT.asItem(), 4,8,
               new  ItemStack(Items.EMERALD,4),
                16,4,0.05f);
        registerTrader(ModVillagers.ASSEMBLER,5,
                Items.EMERALD,56,64,
                AllItems.WRENCH.asStack(),
                1,16,0.05f);
    }

    private static void registerTrader(VillagerProfession villagerProfession, int level,
                                       Item buyItem, int minBuyItemCount,int maxBuyItemCount,
                                       ItemStack sellItemStack,
                                       int maxUses, int merchantExperience , float priceMultiplier ){
        TradeOfferHelper.registerVillagerOffers(villagerProfession, level,
                factories -> {
                    factories.add(((entity, random) -> new TradeOffer(
                            new ItemStack(buyItem,random.nextBetween(minBuyItemCount,maxBuyItemCount)),sellItemStack,maxUses,merchantExperience,priceMultiplier
                    )));
                });
    }
}
