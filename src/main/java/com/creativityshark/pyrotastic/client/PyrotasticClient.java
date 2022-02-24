package com.creativityshark.pyrotastic.client;

import com.creativityshark.pyrotastic.PyrotasticMod;
import com.creativityshark.pyrotastic.client.model.FireworkRocketModel;
import com.creativityshark.pyrotastic.client.particle.CrateSmokeParticle;
import com.creativityshark.pyrotastic.client.renderer.FireworksCrateEntityRenderer;
import com.creativityshark.pyrotastic.common.util.RocketColorHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public class PyrotasticClient implements ClientModInitializer {
    public static final EntityModelLayer FIREWORK_ROCKET_MODEL_LAYER = new EntityModelLayer(new Identifier(PyrotasticMod.MOD_ID, "firework_rocket"), "main");
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(PyrotasticMod.CRATE_ENTITY, FireworksCrateEntityRenderer::new);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 1 ? RocketColorHandler.getColor(stack) : 0xFFFFFFF, Items.FIREWORK_ROCKET);

        EntityModelLayerRegistry.registerModelLayer(FIREWORK_ROCKET_MODEL_LAYER, FireworkRocketModel::getTexturedModelData);

        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) -> registry.register(new Identifier(PyrotasticMod.MOD_ID, "particle/crate_smoke"))));

        ParticleFactoryRegistry.getInstance().register(PyrotasticMod.CRATE_SMOKE, CrateSmokeParticle.SmokeFactory::new);
    }
}
