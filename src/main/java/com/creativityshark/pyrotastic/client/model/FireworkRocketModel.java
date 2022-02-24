//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.creativityshark.pyrotastic.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FireworkRocketEntity;

@Environment(EnvType.CLIENT)
public class FireworkRocketModel extends EntityModel<FireworkRocketEntity> {
    private final ModelPart root;

    public FireworkRocketModel(ModelPart root) {
        super(RenderLayer::getEntitySolid);
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, -7.0F, -1.5F, 3.0F, 13.0F, 3.0F), ModelTransform.NONE);
        modelPartData2.addChild("rim", ModelPartBuilder.create().uv(0, 16).cuboid(-2.5F, -5.0F, -2.5F, 5.0F, 2.0F, 5.0F), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 32, 32);
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void setAngles(FireworkRocketEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }
}
