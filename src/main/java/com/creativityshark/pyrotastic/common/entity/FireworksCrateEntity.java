package com.creativityshark.pyrotastic.common.entity;

import com.creativityshark.pyrotastic.PyrotasticMod;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FireworksCrateEntity extends Entity implements Inventory {
    private static final TrackedData<Integer> FUSE;
    private static final TrackedData<Integer> LEVEL;
    private DefaultedList<ItemStack> inventory;
    @Nullable
    private LivingEntity causingEntity;

    public FireworksCrateEntity(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
        this.intersectionChecked = true;
    }

    public FireworksCrateEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter, DefaultedList<ItemStack> inventory) {
        this(PyrotasticMod.CRATE_ENTITY, world);
        this.setPosition(x, y, z);
        double d = world.random.nextDouble() * 6.2831854820251465D;
        this.setVelocity(-Math.sin(d) * 0.02D, 0.20000000298023224D, -Math.cos(d) * 0.02D);
        this.setFuse(80);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.causingEntity = igniter;
        if(inventory.isEmpty()) {
            this.inventory = DefaultedList.ofSize(16, ItemStack.EMPTY);
        } else {
            this.inventory = inventory;
        }
        this.setLevel();
    }

    protected void initDataTracker() {
        this.dataTracker.startTracking(FUSE, 80);
        this.dataTracker.startTracking(LEVEL, 1);
    }

    protected MoveEffect getMoveEffect() {
        return MoveEffect.NONE;
    }

    public boolean collides() {
        return !this.isRemoved();
    }

    public void tick() {
        if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0D, -0.04D, 0.0D));
        }

        this.move(MovementType.SELF, this.getVelocity());
        this.setVelocity(this.getVelocity().multiply(0.98D));
        if (this.onGround) {
            this.setVelocity(this.getVelocity().multiply(0.7D, -0.5D, 0.7D));
        }

        int i = this.getFuse() - 1;
        this.setFuse(i);
        if (i <= 0) {
            this.discard();
            if (!this.world.isClient) {
                if (!this.isEmpty()) {
                    this.explode();
                }
            }
        } else {
            this.updateWaterState();
            if (this.world.isClient && this.getFuse() % 2 == 0) {
                this.world.addParticle(PyrotasticMod.CRATE_SMOKE, this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }

    }

    private void explode() {
        if(!this.isEmpty()) {
            for(int i = 0; i < this.world.random.nextInt(3) + 3; i++) {
                ItemStack stack = ItemStack.EMPTY;
                for(ItemStack stack1:this.getInventory()) {
                    if(!this.isEmpty()) {
                        if(!stack1.isEmpty()) {
                            if(stack.isEmpty()) {
                                stack = stack1;
                            } else {
                                stack = (this.world.random.nextDouble() <= 0.5 ? stack1 : stack);
                            }
                        }
                    }
                }
                ItemStack stack1 = stack.copy();
                stack.setCount((Math.max(stack.getCount() - 1, 0)));
                stack1.setCount(1);
                if(stack1.isOf(Items.FIREWORK_ROCKET)) {
                    FireworkRocketEntity rocket = new FireworkRocketEntity(this.world, this.getX(), this.getY(), this.getZ(), stack1);
                    if(this.getCausingEntity() != null) {
                        rocket.setOwner(this.getCausingEntity());
                    }
                    rocket.setVelocity(rocket.getVelocity().getX() * 10, rocket.getVelocity().getY() * 10, rocket.getVelocity().getZ() * 10);
                    rocket.addVelocity(this.getVelocity().getX() / 30, this.getVelocity().getY() / 30, this.getVelocity().getZ() / 30);
                    this.world.spawnEntity(rocket);
                } else if(stack1.isOf(Items.TNT)) {
                    TntEntity tnt = new TntEntity(this.world, this.getX(), this.getY(), this.getZ(), this.getCausingEntity());
                    tnt.setVelocity(this.getVelocity());
                    tnt.addVelocity(-Math.sin(this.world.random.nextDouble() * 6.2831854820251465D) * 0.02D, 0.20000000298023224D, -Math.cos(this.world.random.nextDouble() * 6.2831854820251465D) * 0.02D);
                    tnt.setFuse(80 + (this.world.random.nextInt(20) - 10));
                    this.world.spawnEntity(tnt);
                } else {
                    ItemEntity item = new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), stack1);
                    item.setVelocity(this.getVelocity());
                    item.addVelocity(this.world.random.nextDouble() * 2 - 1, 0.5, this.world.random.nextDouble() * 2 - 1);
                    this.world.spawnEntity(item);
                }
                this.world.playSound(null, this.getX(), this.getY(), this.getZ(), PyrotasticMod.FIREWORKS_CRATE_CLOSE, SoundCategory.NEUTRAL, 1.0F, 1.0F);

            }
            if(!this.isEmpty()) {
                FireworksCrateEntity crateEntity = new FireworksCrateEntity(this.world, this.getX(), this.getY(), this.getZ(), this.getCausingEntity(), this.getInventory());
                crateEntity.setVelocity(this.getVelocity());
                crateEntity.addVelocity((this.world.random.nextDouble() / 8) * (this.world.random.nextBoolean() ? 1:-1), 0.25, (this.world.random.nextDouble() / 8) * (this.world.random.nextBoolean() ? 1:-1));
                this.world.spawnEntity(crateEntity);
            }
        }
    }

    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putShort("Fuse", (short)this.getFuse());
        nbt.putShort("Level", (short) this.getLevel());
        if(this.inventory != null && !this.inventory.isEmpty()) {
            Inventories.writeNbt(nbt, this.inventory);
        }
    }

    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.setFuse(nbt.getShort("Fuse"));
        this.setLevel(nbt.getShort("Level"));
        if(this.inventory != null && !this.inventory.isEmpty()) {
            Inventories.readNbt(nbt, this.inventory);
        }
    }

    @Nullable
    public LivingEntity getCausingEntity() {
        return this.causingEntity;
    }

    protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.15F;
    }

    public void setFuse(int fuse) {
        this.dataTracker.set(FUSE, fuse);
    }

    public int getFuse() {
        return this.dataTracker.get(FUSE);
    }

    public void setLevel(int level) {
        this.dataTracker.set(LEVEL, level);
    }

    public void setLevel() {
        World world = this.getWorld();
        if(world != null) {
            if(this.isEmpty()) {
                this.setLevel(1);
            } else if(this.getTotalCount() <= (float) this.size() / 2) {
                this.setLevel(2);
            } else {
                this.setLevel(3);
            }
        }
    }

    public int getLevel() {
        return this.dataTracker.get(LEVEL);
    }

    public DefaultedList<ItemStack> getInventory() {
        return this.inventory;
    }

    public double getTotalCount() {
        int result = 0;
        for (ItemStack stack : this.inventory) {
            result += stack.getCount() / ((double)stack.getMaxCount() / 64);
        }
        return result;
    }

    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        if(this.inventory == null) {
            return true;
        }
        for (ItemStack stack: this.inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
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

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    static {
        FUSE = DataTracker.registerData(FireworksCrateEntity.class, TrackedDataHandlerRegistry.INTEGER);
        LEVEL = DataTracker.registerData(FireworksCrateEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }
}
