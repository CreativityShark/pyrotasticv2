package com.creativityshark.pyrotastic.common.item;

import net.minecraft.block.Block;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class FireworksCrateBlockItem extends BlockItem {
    private static final int ITEM_BAR_COLOR = MathHelper.packRgb(0.4F, 0.4F, 1.0F);

    public FireworksCrateBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    public boolean isItemBarVisible(ItemStack stack) {
        return getCrateOccupancy(stack) > 0;
    }

    public int getItemBarStep(ItemStack stack) {
        return Math.min(1 + 12 * getCrateOccupancy(stack) / 16, 13);
    }

    public int getItemBarColor(ItemStack stack) {
        return ITEM_BAR_COLOR;
    }

    private static Stream<ItemStack> getCrateStacks(ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateSubNbt("BlockEntityTag");
        if (nbtCompound == null) {
            return Stream.empty();
        } else {
            NbtList nbtList = nbtCompound.getList("Items", 10);
            Stream<NbtElement> var10000 = nbtList.stream();
            Objects.requireNonNull(NbtCompound.class);
            return var10000.map(NbtCompound.class::cast).map(ItemStack::fromNbt);
        }
    }

    private static int getCrateOccupancy(ItemStack stack) {
        return getCrateStacks(stack).mapToInt((itemStack) -> getItemOccupancy(itemStack) * itemStack.getCount()).sum();
    }

    private static int getItemOccupancy(ItemStack stack) {
        if (stack.isOf(Items.BUNDLE)) {
            return 4 + getCrateOccupancy(stack);
        } else {
            if ((stack.isOf(Items.BEEHIVE) || stack.isOf(Items.BEE_NEST)) && stack.hasNbt()) {
                NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
                if (nbtCompound != null && !nbtCompound.getList("Bees", 10).isEmpty()) {
                    return 64;
                }
            }

            return 64 / stack.getMaxCount();
        }
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        if(getCrateOccupancy(stack) > 0) {
            DefaultedList<ItemStack> defaultedList = DefaultedList.of();
            Stream<ItemStack> var10000 = getCrateStacks(stack);
            Objects.requireNonNull(defaultedList);
            var10000.forEach(defaultedList::add);
            return Optional.of(new BundleTooltipData(defaultedList, getCrateOccupancy(stack)));
        }
        return Optional.empty();
    }

    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if(stack.getSubNbt("BlockItemTag") != null) {
            tooltip.add((new TranslatableText("item.minecraft.bundle.fullness", getCrateOccupancy(stack), 16)).formatted(Formatting.GRAY));
        }
    }
}
