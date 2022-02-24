package com.creativityshark.pyrotastic.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends Entity {
    @Shadow private int life;

    public FireworkRocketEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = {"tick"}, at = @At("TAIL"))
    private void tickInject(CallbackInfo info) {
        if(this.world.isClient && this.life % 2 == 0) {
            this.world.addParticle(ParticleTypes.FLAME, this.getX(), this.getY() - 0.3D, this.getZ(), this.getVelocity().getX(), this.getVelocity().getY() * 0.2, this.getVelocity().getZ());
        }
    }
}
