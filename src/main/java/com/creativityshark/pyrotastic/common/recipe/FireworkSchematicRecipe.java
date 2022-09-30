//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.creativityshark.pyrotastic.common.recipe;

import com.creativityshark.pyrotastic.PyrotasticMod;
import com.google.common.collect.Maps;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.FireworkRocketItem.Type;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Map;

public class FireworkSchematicRecipe extends SpecialCraftingRecipe {
    private static final Ingredient TYPE_MODIFIER;
    private static final Ingredient TRAIL_MODIFIER;
    private static final Ingredient FLICKER_MODIFIER;
    private static final Ingredient PAPER;
    private static final Map TYPE_MODIFIER_MAP;

    public FireworkSchematicRecipe(Identifier identifier) {
        super(identifier);
    }

    public boolean matches(CraftingInventory craftingInventory, World world) {
        boolean schematicBool = false;
        boolean typeBool = false;
        boolean trailBool = false;
        boolean flickerBool = false;

        for(int i = 0; i < craftingInventory.size(); ++i) {
            ItemStack itemStack = craftingInventory.getStack(i);
            if (!itemStack.isEmpty()) {
                if (TYPE_MODIFIER.test(itemStack)) {
                    if (typeBool) {
                        return false;
                    }
                    typeBool = true;
                } else if (FLICKER_MODIFIER.test(itemStack)) {
                    if (flickerBool) {
                        return false;
                    }
                    flickerBool = true;
                } else if (TRAIL_MODIFIER.test(itemStack)) {
                    if (trailBool) {
                        return false;
                    }
                    trailBool = true;
                } else if (PAPER.test(itemStack)) {
                    if (schematicBool) {
                        return false;
                    }
                    schematicBool = true;
                } else {
                    return false;
                }
            }
        }
        return schematicBool && (typeBool || flickerBool || trailBool);
    }

    public ItemStack craft(CraftingInventory craftingInventory) {
        ItemStack itemStack = new ItemStack(PyrotasticMod.FIREWORK_SCHEMATIC);
        NbtCompound nbtCompound = itemStack.getOrCreateSubNbt("Explosion");
        Type type = Type.SMALL_BALL;
        for(int i = 0; i < craftingInventory.size(); ++i) {
            ItemStack itemStack2 = craftingInventory.getStack(i);
            if (!itemStack2.isEmpty()) {
                if (TYPE_MODIFIER.test(itemStack2)) {
                    type = (Type)TYPE_MODIFIER_MAP.get(itemStack2.getItem());
                } else if (FLICKER_MODIFIER.test(itemStack2)) {
                    nbtCompound.putBoolean("Flicker", true);
                } else if (TRAIL_MODIFIER.test(itemStack2)) {
                    nbtCompound.putBoolean("Trail", true);
                }
            }
        }
        nbtCompound.putByte("Type", (byte)type.getId());
        return itemStack;
    }

    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(CraftingInventory inventory) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
        for(int i = 0; i < defaultedList.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.getItem().equals(Items.CREEPER_BANNER_PATTERN)) {
                defaultedList.set(i, itemStack.copy());
                break;
            }
        }
        return defaultedList;
    }

    public ItemStack getOutput() {
        return new ItemStack(PyrotasticMod.FIREWORK_SCHEMATIC);
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_STAR;
    }

    static {
        TYPE_MODIFIER = Ingredient.ofItems(Items.FIRE_CHARGE, Items.FEATHER, Items.GOLD_NUGGET, Items.CREEPER_BANNER_PATTERN);
        TRAIL_MODIFIER = Ingredient.ofItems(Items.DIAMOND);
        FLICKER_MODIFIER = Ingredient.ofItems(Items.GLOWSTONE_DUST);
        PAPER = Ingredient.ofItems(Items.PAPER, PyrotasticMod.FIREWORK_SCHEMATIC);
        TYPE_MODIFIER_MAP = Util.make(Maps.newHashMap(), (hashMap) -> {
            hashMap.put(Items.FIRE_CHARGE, Type.LARGE_BALL);
            hashMap.put(Items.FEATHER, Type.BURST);
            hashMap.put(Items.GOLD_NUGGET, Type.STAR);
            hashMap.put(Items.CREEPER_BANNER_PATTERN, Type.CREEPER);
        });
    }
}
