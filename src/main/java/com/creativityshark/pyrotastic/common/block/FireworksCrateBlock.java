package com.creativityshark.pyrotastic.common.block;

import com.creativityshark.pyrotastic.PyrotasticMod;
import com.creativityshark.pyrotastic.common.entity.FireworksCrateEntity;
import com.creativityshark.pyrotastic.common.entity.FireworksCrateBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class FireworksCrateBlock extends HorizontalFacingBlock implements BlockEntityProvider {
    public static final IntProperty LEVEL;
    public static final BooleanProperty UNSTABLE;

    public FireworksCrateBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(LEVEL, 1).with(FACING, Direction.NORTH).with(UNSTABLE, false));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FireworksCrateBlockEntity(pos, state);
    }

    public static void primeCrate(World world, BlockPos pos) {
        primeCrate(world, pos, null);
    }

    private static void primeCrate(World world, BlockPos pos, @Nullable LivingEntity igniter) {
        if (!world.isClient) {
            if(world.getBlockEntity(pos) instanceof FireworksCrateBlockEntity crateBlockEntity) {
                FireworksCrateEntity crateEntity = new FireworksCrateEntity(world, (double)pos.getX() + 0.5D, pos.getY(), (double)pos.getZ() + 0.5D, igniter, crateBlockEntity.getInventory());
                world.spawnEntity(crateEntity);
                world.playSound(null, crateEntity.getX(), crateEntity.getY(), crateEntity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent(igniter, GameEvent.PRIME_FUSE, pos);
            }
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (world.isReceivingRedstonePower(pos) && world.getBlockEntity(pos) instanceof FireworksCrateBlockEntity crateEntity && !crateEntity.isEmpty()) {
            primeCrate(world, pos);
            world.removeBlock(pos, false);
        }

    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock())) {
            if (world.isReceivingRedstonePower(pos) && world.getBlockEntity(pos) instanceof FireworksCrateBlockEntity crateEntity && !crateEntity.isEmpty()) {
                primeCrate(world, pos);
                world.removeBlock(pos, false);
            }
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if(world.getBlockEntity(pos) instanceof FireworksCrateBlockEntity) {
            updateFilledLevel((FireworksCrateBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos)));
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (!world.isClient) {
            BlockPos blockPos = hit.getBlockPos();
            Entity entity = projectile.getOwner();
            if (projectile.isOnFire() && projectile.canModifyAt(world, blockPos) && world.getBlockEntity(blockPos) instanceof FireworksCrateBlockEntity crateEntity && !crateEntity.isEmpty()) {
                primeCrate(world, blockPos, entity instanceof LivingEntity ? (LivingEntity)entity : null);
                world.removeBlock(blockPos, false);
            }
        }

    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if(world instanceof ServerWorld && world.getBlockEntity(pos) instanceof FireworksCrateBlockEntity crateEntity && !player.isCreative()) {
            ItemStack itemStack = new ItemStack(PyrotasticMod.FIREWORKS_CRATE);
            if(crateEntity.isEmpty()) {
                itemStack.getOrCreateSubNbt("BlockEntityTag");
            } else {
                crateEntity.setStackNbt(itemStack);
            }
            ItemEntity itemEntity = new ItemEntity(world,
                    (double)pos.getX() + 0.5D + MathHelper.nextDouble(world.random, -0.25D, 0.25D),
                    (double)pos.getY() + 0.5D + MathHelper.nextDouble(world.random, -0.25D, 0.25D - (EntityType.ITEM.getHeight() / 2.0F)),
                    (double)pos.getZ() + 0.5D + MathHelper.nextDouble(world.random, -0.25D, 0.25D),
                    itemStack);
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
        }
        if (!world.isClient() && !player.isCreative() && state.get(UNSTABLE) && world.getBlockEntity(pos) instanceof FireworksCrateBlockEntity crateEntity && !crateEntity.isEmpty()) {
            primeCrate(world, pos);
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        if (!world.isClient) {
            if(world.getBlockEntity(pos) instanceof FireworksCrateBlockEntity crateBlockEntity) {
                FireworksCrateEntity crateEntity = new FireworksCrateEntity(world, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, explosion.getCausingEntity(), crateBlockEntity.getInventory());
                int i = crateEntity.getFuse();
                crateEntity.setFuse((short)(world.random.nextInt(i / 4) + i / 8));
                world.spawnEntity(crateEntity);
            }
        }
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        player.incrementStat(Stats.MINED.getOrCreateStat(this));
        player.addExhaustion(0.005F);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof FireworksCrateBlockEntity crateEntity) {
            if((stack.isOf(Items.FIREWORK_ROCKET) || stack.isOf(Items.TNT) || stack.isOf(Items.EGG)) && !crateEntity.isFull() && !(crateEntity.getTotalCount() + crateEntity.getSpecificCount(stack) > 16)) {
                crateEntity.addStack(stack.copy());
                stack.setCount(0);
                world.playSound(player, pos, PyrotasticMod.FIREWORKS_CRATE_OPEN, SoundCategory.BLOCKS, 1f, 1f);
                world.updateComparators(pos, this);
                updateFilledLevel(crateEntity);
                return ActionResult.SUCCESS;
            } else if((stack.isOf(Items.FLINT_AND_STEEL) || stack.isOf(Items.FIRE_CHARGE)) && !crateEntity.isEmpty()) {
                primeCrate(world, pos, player);
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
                Item item = stack.getItem();
                if (!player.isCreative()) {
                    if (stack.isOf(Items.FLINT_AND_STEEL)) {
                        stack.damage(1, player, (playerx) -> {
                            playerx.sendToolBreakStatus(hand);
                        });
                    } else {
                        stack.decrement(1);
                    }
                }

                player.incrementStat(Stats.USED.getOrCreateStat(item));
                return ActionResult.success(world.isClient);
            } else if(stack.isEmpty() && !crateEntity.isEmpty()) {
                ItemStack itemStack = crateEntity.removeLatestStack();
                if(itemStack != null) {
                    player.getInventory().offerOrDrop(itemStack);
                    world.playSound(player, pos, PyrotasticMod.FIREWORKS_CRATE_CLOSE, SoundCategory.BLOCKS, 1f, 1f);
                    world.updateComparators(pos, this);
                    updateFilledLevel(crateEntity);
                    return ActionResult.SUCCESS;
                } else {
                    return super.onUse(state, world, pos, player, hand, hit);
                }
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FireworksCrateBlockEntity) {
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if(world.getBlockEntity(pos) instanceof FireworksCrateBlockEntity crateEntity) {
            if(crateEntity.getInventory() == null || crateEntity.isEmpty()) {
                return 0;
            } else {
                return MathHelper.floor(crateEntity.getTotalCount() / crateEntity.size() * 14 + 1);
            }
        } else {
            return super.getComparatorOutput(state, world, pos);
        }
    }

    public void updateFilledLevel(FireworksCrateBlockEntity crateEntity) throws IllegalArgumentException {
        World world = crateEntity.getWorld();
        BlockPos pos = crateEntity.getPos();
        if(world != null) {
            BlockState state = world.getBlockState(pos);
            if(crateEntity.isEmpty()) {
                world.setBlockState(pos, state.with(LEVEL, 1));
            } else if(crateEntity.getTotalCount() <= (float) crateEntity.size() / 2) {
                world.setBlockState(pos, state.with(LEVEL, 2));
            } else {
                world.setBlockState(pos, state.with(LEVEL, 3));
            }
        }
    }

    public boolean shouldDropItemsOnExplosion(Explosion explosion) {
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(LEVEL);
        builder.add(UNSTABLE);
        super.appendProperties(builder);
    }

    static {
        LEVEL = Properties.LEVEL_3;
        UNSTABLE = Properties.UNSTABLE;
    }
}
