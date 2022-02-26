package com.creativityshark.pyrotastic.mixin;

import com.creativityshark.pyrotastic.PyrotasticMod;
import com.creativityshark.pyrotastic.client.PyrotasticClient;
import com.creativityshark.pyrotastic.client.model.FireworkRocketModel;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FireworkRocketEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//I just sort of messed around with the trident renderer code until it sort of worked?? IDK??
@Mixin(FireworkRocketEntityRenderer.class)
public abstract class FireworkRocketEntityRendererMixin extends EntityRenderer<FireworkRocketEntity> {
    private static final Identifier TEXTURE = new Identifier(PyrotasticMod.MOD_ID, "textures/entity/projectile/firework_rocket.png");
    private FireworkRocketModel model;

    protected FireworkRocketEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/render/entity/EntityRendererFactory$Context;)V", at = @At("TAIL"))
    private void constructorInject(EntityRendererFactory.Context context, CallbackInfo info) {
        this.model = new FireworkRocketModel(context.getPart(PyrotasticClient.FIREWORK_ROCKET_MODEL_LAYER));
    }

    @Inject(method = "render(Lnet/minecraft/entity/projectile/FireworkRocketEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    private void renderInject(FireworkRocketEntity fireworkRocketEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        matrixStack.push();
        if(fireworkRocketEntity.wasShotAtAngle()) {
            matrixStack.multiply(this.dispatcher.getRotation());
            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
        }
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
        matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
        VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumerProvider, this.model.getLayer(this.getTexture(fireworkRocketEntity)), false, false);
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pop();
        super.render(fireworkRocketEntity, f, g, matrixStack, vertexConsumerProvider, i);
        ci.cancel();
    }

    @Inject(method = "getTexture(Lnet/minecraft/entity/projectile/FireworkRocketEntity;)Lnet/minecraft/util/Identifier;", at = @At("RETURN"), cancellable = true)
    private void getTextureInject(FireworkRocketEntity fireworkRocketEntity, CallbackInfoReturnable<Identifier> cir) {
        cir.setReturnValue(TEXTURE);
    }
}
