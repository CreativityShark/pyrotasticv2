package com.creativityshark.pyrotastic;

import com.creativityshark.pyrotastic.common.block.FireworksCrateBlock;
import com.creativityshark.pyrotastic.common.entity.FireworksCrateBlockEntity;
import com.creativityshark.pyrotastic.common.item.FireworkSchematicItem;
import com.creativityshark.pyrotastic.common.item.FireworksCrateBlockItem;
import com.creativityshark.pyrotastic.common.entity.FireworksCrateEntity;
import com.creativityshark.pyrotastic.common.recipe.FireworkSchematicRecipe;
import com.creativityshark.pyrotastic.common.recipe.StarAndSchematicRecipe;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//Thanks, https://github.com/poombus. Wouldn't be here without you

public class PyrotasticMod implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("Pyrotastic");
	public static final String MOD_ID = "pyrotastic";

	public static final Item FIREWORK_SCHEMATIC = new FireworkSchematicItem(new FabricItemSettings().group(ItemGroup.MISC).maxCount(1));

	public static final Block FIREWORKS_CRATE = Registry.register(Registry.BLOCK,
			new Identifier(MOD_ID, "fireworks_crate"),
			new FireworksCrateBlock(FabricBlockSettings.of(Material.WOOD).strength(0.5f, 0.5f)));

	public static final BlockEntityType<FireworksCrateBlockEntity> CRATE_BLOCK_ENTITY =  Registry.register(Registry.BLOCK_ENTITY_TYPE,
			new Identifier(MOD_ID, "fireworks_crate"),
			FabricBlockEntityTypeBuilder.create(FireworksCrateBlockEntity::new, FIREWORKS_CRATE).build(null));

	public static final EntityType<FireworksCrateEntity> CRATE_ENTITY = Registry.register(Registry.ENTITY_TYPE,
			new Identifier(MOD_ID, "fireworks_crate"),
			FabricEntityTypeBuilder.<FireworksCrateEntity>create(SpawnGroup.MISC, FireworksCrateEntity::new).dimensions(EntityDimensions.fixed(1f, 1f)).fireImmune().build());

	public static final SpecialRecipeSerializer<FireworkSchematicRecipe> FIREWORK_STAR_RECIPE = Registry.register(Registry.RECIPE_SERIALIZER,
			new Identifier(MOD_ID, "crafting_special_firework_schematic"),
			new SpecialRecipeSerializer<>(FireworkSchematicRecipe::new));

	public static final SpecialRecipeSerializer<StarAndSchematicRecipe> FIREWORK_SCHEMATIC_RECIPE = Registry.register(Registry.RECIPE_SERIALIZER,
			new Identifier(MOD_ID, "crafting_special_firework_star"),
			new SpecialRecipeSerializer<>(StarAndSchematicRecipe::new));

	public static final DefaultParticleType CRATE_SMOKE = FabricParticleTypes.simple();

    public static SoundEvent FIREWORKS_CRATE_CLOSE = new SoundEvent(new Identifier(MOD_ID, "fireworks_crate_close"));
    public static SoundEvent FIREWORKS_CRATE_OPEN = new SoundEvent(new Identifier(MOD_ID, "fireworks_crate_open"));

	@Override
	public void onInitialize() {
		LOGGER.info("registering for " + MOD_ID);

		Registry.register(Registry.ITEM,
				new Identifier(MOD_ID, "firework_schematic"),
				FIREWORK_SCHEMATIC);
		Registry.register(Registry.ITEM,
				new Identifier(MOD_ID, "fireworks_crate"),
				new FireworksCrateBlockItem(FIREWORKS_CRATE,
						new FabricItemSettings().group(ItemGroup.REDSTONE)));

		Registry.register(Registry.PARTICLE_TYPE, new Identifier(MOD_ID, "crate_smoke"), CRATE_SMOKE);

        Registry.register(Registry.SOUND_EVENT, new Identifier(MOD_ID, "fireworks_crate_close"), FIREWORKS_CRATE_CLOSE);
        Registry.register(Registry.SOUND_EVENT, new Identifier(MOD_ID, "fireworks_crate_open"), FIREWORKS_CRATE_OPEN);
	}
}
