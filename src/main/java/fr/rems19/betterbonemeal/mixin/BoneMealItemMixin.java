package fr.rems19.betterbonemeal.mixin;

import fr.rems19.betterbonemeal.BetterBonemeal;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {

    @Inject(at = @At("HEAD"), method = "useOnBlock", cancellable = true)
    private void breakAndReplaceIfMature(ItemUsageContext context, CallbackInfoReturnable<ActionResult> callbackInfo) {

        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();

        // We only care about crops and want to modify the world server-side
        if (block instanceof CropBlock && !world.isClient()) {
            CropBlock crop = (CropBlock) block;

            // If a crop is not mature yet, we don't do anything, the normal behavior proceeds. If it is, we do our thing.
            if (crop.isMature(blockState)) {
                // Get the item to remove from drops to be planted back
                Item seed = BetterBonemeal.seedsTable.get(crop.getClass());
                if (seed != null) {
                    // We generate the drops from the block if it was breaked...
                    List<ItemStack> drops = Block.getDroppedStacks(blockState, (ServerWorld) world, blockPos, null);
                    boolean foundSeedInDrops = false;
                    for (ItemStack is : drops) {
                        if (is.getItem().equals(seed)) {
                            // ...and remove one seed from them
                            is.decrement(1);
                            foundSeedInDrops = true;
                        }
                    }
                    // If there is no seed to replant, then don't do anything. Else, replace the block with age 0 and drop the remaining drops.
                    if (foundSeedInDrops) {
                        Block.replaceBlock(blockState, crop.withAge(0), world, blockPos, 0, 1);
                        drops.forEach(is -> Block.dropStack(world, blockPos, is));

                        // We don't want the default behavior to happen after that, the player will have to click again.
                        callbackInfo.setReturnValue(ActionResult.SUCCESS);
                    }
                }
            }
        }
    }
}
