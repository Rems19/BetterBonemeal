package fr.rems19.betterbonemeal;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.BeetrootsBlock;
import net.minecraft.block.CarrotsBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.PotatoesBlock;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;

public class BetterBonemeal implements ModInitializer {

    public static final String MOD_ID = "betterbonemeal";

    public static Map<Class<? extends CropBlock>, Item> seedsTable = new HashMap<>();

    @Override
    public void onInitialize() {
        seedsTable.put(CropBlock.class, Items.WHEAT_SEEDS);
        seedsTable.put(CarrotsBlock.class, Items.CARROT);
        seedsTable.put(PotatoesBlock.class, Items.POTATO);
        seedsTable.put(BeetrootsBlock.class, Items.BEETROOT_SEEDS);
    }
}
