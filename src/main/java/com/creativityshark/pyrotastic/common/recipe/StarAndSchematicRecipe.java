//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.creativityshark.pyrotastic.common.recipe;

import com.creativityshark.pyrotastic.PyrotasticMod;
import com.creativityshark.pyrotastic.common.item.FireworkSchematicItem;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.*;
import net.minecraft.item.FireworkRocketItem.Type;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class StarAndSchematicRecipe extends SpecialCraftingRecipe {
    private static final Ingredient TYPE_MODIFIER;
    private static final Ingredient TRAIL_MODIFIER;
    private static final Ingredient FLICKER_MODIFIER;
    private static final Ingredient SCHEMATIC;
    private static final Map TYPE_MODIFIER_MAP;
    private static final Ingredient GUNPOWDER;

    public StarAndSchematicRecipe(Identifier identifier) {
        super(identifier);
    }

    public boolean matches(CraftingInventory craftingInventory, World world) {
        boolean gunpowderBool = false;
        boolean dyeBool = false;
        boolean typeBool = false;
        boolean flickerBool = false;
        boolean trailBool = false;
        boolean schematicBool = false;

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
                } else if (GUNPOWDER.test(itemStack)) {
                    if (gunpowderBool) {
                        return false;
                    }

                    gunpowderBool = true;
                } else if (SCHEMATIC.test(itemStack) && itemStack.getSubNbt("Explosion") != null) {
                    if (schematicBool) {
                        return false;
                    } else {
                        NbtCompound nbtCompound = itemStack.getSubNbt("Explosion");
                        assert nbtCompound != null;
                        if (nbtCompound.getByte("Type") != 0) {
                            PyrotasticMod.LOGGER.info("hehah");
                            if (typeBool) {
                                return false;
                            }
                            typeBool = true;
                        } else if (nbtCompound.getBoolean("Flicker")) {
                            if (flickerBool) {
                                return false;
                            }
                            flickerBool = true;
                        } else if (nbtCompound.getBoolean("Trail")) {
                            if (trailBool) {
                                return false;
                            }
                            trailBool = true;
                        }
                    }
                    schematicBool = true;
                } else {
                    if (!(itemStack.getItem() instanceof DyeItem)) {
                        return false;
                    }

                    dyeBool = true;
                }
            }
        }
        return gunpowderBool && dyeBool;
    }

    public ItemStack craft(CraftingInventory craftingInventory) {
        ItemStack itemStack = new ItemStack(Items.FIREWORK_STAR);
        NbtCompound nbtCompound = itemStack.getOrCreateSubNbt("Explosion");
        Type type = Type.SMALL_BALL;
        List<Integer> list = Lists.newArrayList();

        for(int i = 0; i < craftingInventory.size(); ++i) {
            ItemStack itemStack2 = craftingInventory.getStack(i);
            if (!itemStack2.isEmpty()) {
                if (TYPE_MODIFIER.test(itemStack2)) {
                    type = (Type)TYPE_MODIFIER_MAP.get(itemStack2.getItem());
                } else if (FLICKER_MODIFIER.test(itemStack2)) {
                    nbtCompound.putBoolean("Flicker", true);
                } else if (TRAIL_MODIFIER.test(itemStack2)) {
                    nbtCompound.putBoolean("Trail", true);
                } else if (SCHEMATIC.test(itemStack2)) {
                    NbtCompound nbtCompound2 = itemStack2.getSubNbt("Explosion");
                    assert nbtCompound2 != null;
                    if (nbtCompound2.getByte("Type") != 0) {
                        type = FireworkRocketItem.Type.byId(nbtCompound2.getByte("Type"));
                    }
                    if (nbtCompound2.getBoolean("Flicker")) {
                        nbtCompound.putBoolean("Flicker", true);
                    }
                    if (nbtCompound2.getBoolean("Trail")) {
                        nbtCompound.putBoolean("Trail", true);
                    }
                } else if (itemStack2.getItem() instanceof DyeItem) {
                    list.add(((DyeItem)itemStack2.getItem()).getColor().getFireworkColor());
                }
            }
        }

        nbtCompound.putIntArray("Colors", list);
        nbtCompound.putByte("Type", (byte)type.getId());
        return itemStack;
    }

    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    public ItemStack getOutput() {
        return new ItemStack(Items.FIREWORK_STAR);
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(CraftingInventory inventory) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
        for(int i = 0; i < defaultedList.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.getItem() instanceof FireworkSchematicItem) {
                defaultedList.set(i, itemStack.copy());
                break;
            }
        }
        return defaultedList;
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_STAR;
    }

    static {
        TYPE_MODIFIER = Ingredient.ofItems(Items.FIRE_CHARGE, Items.FEATHER, Items.GOLD_NUGGET, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.CREEPER_HEAD, Items.PLAYER_HEAD, Items.DRAGON_HEAD, Items.ZOMBIE_HEAD);
        TRAIL_MODIFIER = Ingredient.ofItems(Items.DIAMOND);
        FLICKER_MODIFIER = Ingredient.ofItems(Items.GLOWSTONE_DUST);
        SCHEMATIC = Ingredient.ofItems(PyrotasticMod.FIREWORK_SCHEMATIC);
        TYPE_MODIFIER_MAP = Util.make(Maps.newHashMap(), (hashMap) -> {
            hashMap.put(Items.FIRE_CHARGE, Type.LARGE_BALL);
            hashMap.put(Items.FEATHER, Type.BURST);
            hashMap.put(Items.GOLD_NUGGET, Type.STAR);
            hashMap.put(Items.SKELETON_SKULL, Type.CREEPER);
            hashMap.put(Items.WITHER_SKELETON_SKULL, Type.CREEPER);
            hashMap.put(Items.CREEPER_HEAD, Type.CREEPER);
            hashMap.put(Items.PLAYER_HEAD, Type.CREEPER);
            hashMap.put(Items.DRAGON_HEAD, Type.CREEPER);
            hashMap.put(Items.ZOMBIE_HEAD, Type.CREEPER);
        });
        GUNPOWDER = Ingredient.ofItems(Items.GUNPOWDER);
    }
}
