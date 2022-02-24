package com.creativityshark.pyrotastic.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

//net.minecraft.client.particle.CrateSmokeParticle but with different movement
public class CrateSmokeParticle extends SpriteBillboardParticle {
    CrateSmokeParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z);
        this.maxAge = this.random.nextInt(50) + 80;
        this.scale(3.0F);
        this.setBoundingBoxSpacing(0.25F, 0.25F);
        this.velocityX = velocityX + (double)((this.random.nextFloat() / 50.0F) * (this.random.nextBoolean() ? 1 : -1));
        this.velocityY = velocityY;
        this.velocityZ = velocityZ + (double)((this.random.nextFloat() / 50.0F) * (this.random.nextBoolean() ? 1 : -1));
    }

    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ < this.maxAge && !(this.alpha <= 0.0F)) {
            this.velocityX += this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1);
            this.velocityZ += this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1);
            this.move(this.velocityX, this.velocityY, this.velocityZ);
            if (this.age >= this.maxAge - 60 && this.alpha > 0.01F) {
                this.alpha -= 0.015F;
            }
        } else {
            this.markDead();
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class SmokeFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public SmokeFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            CrateSmokeParticle crateSmokeParticle = new CrateSmokeParticle(clientWorld, d, e, f, g, h, i);
            crateSmokeParticle.setAlpha(0.95F);
            crateSmokeParticle.setSprite(this.spriteProvider);
            return crateSmokeParticle;
        }
    }
}
