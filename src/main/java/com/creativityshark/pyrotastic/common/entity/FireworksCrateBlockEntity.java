package com.creativityshark.pyrotastic.common.entity;

import com.creativityshark.pyrotastic.PyrotasticMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class FireworksCrateBlockEntity  extends BlockEntity implements Inventory {
    private final DefaultedList<ItemStack> inventory;

    public FireworksCrateBlockEntity(BlockPos pos, BlockState state) {
        super(PyrotasticMod.CRATE_BLOCK_ENTITY, pos, state);
        this.inventory = DefaultedList.ofSize(16, ItemStack.EMPTY);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, inventory);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(getInventory(), slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(inventory, slot);
    }

    //removes latest stack in inventory (highest index)
    public ItemStack removeLatestStack() {
        if(this.isEmpty()) {
            PyrotasticMod.LOGGER.warn("inventory is empty, could not take from " + this);
            return null;
        }
        ItemStack stack = null;
        for(ItemStack stack1:this.inventory) {
            if(!stack1.isEmpty()) {
                stack = stack1;
            }
        }
        if(stack != null) {
            ItemStack stack1 = stack.copy();
            stack.setCount(0);
            return stack1;
        }
        return null;
    }

    public void addStack(ItemStack stack) {
        if(this.isFull()) {
            PyrotasticMod.LOGGER.warn("inventory full, could not add " + stack + " to " + this);
        }
        for(int i = 0; i < this.inventory.size(); i++) {
            if(this.inventory.get(i).isEmpty()) {
                this.inventory.set(i, stack);
                break;
            } /*check if can merge with existing stack*/ else if(this.inventory.get(i).isOf(stack.getItem()) &&
                    this.inventory.get(i).getCount() + stack.getCount() <= stack.getItem().getMaxCount() &&
                    this.inventory.get(i).getNbt() == stack.getNbt()) {
                this.inventory.get(i).setCount(this.inventory.get(i).getCount() + stack.getCount());
                break;
            }
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack: this.inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isFull() {
        if(this.getTotalCount() >= 16) {
            return true;
        }
        for (ItemStack stack : this.inventory) {
            if (stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    //returns the "space" taken up, determined by stack size
    //for example, cobblestone takes up 1 space, and egg takes up 4, and a tool takes up 64
    public double getTotalCount() {
        int result = 0;
        for (ItemStack stack : this.inventory) {
            result += stack.getCount() / ((double)stack.getMaxCount() / 64);
        }
        return result;
    }

    public DefaultedList<ItemStack> getInventory() {
        return this.inventory;
    }

    public double getSpecificCount(ItemStack stack) {
        return stack.getCount() / ((double)stack.getMaxCount() / 64);
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }
}
