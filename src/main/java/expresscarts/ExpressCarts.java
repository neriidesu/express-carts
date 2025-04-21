package expresscarts;

import dev.xpple.betterconfig.api.ModConfigBuilder;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.MinecartDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.DispenserBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpressCarts implements ModInitializer {
	public static final String MOD_ID = "expresscarts";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final EntityType<ExpressMinecartEntity> EXPRESS_MINECART_ENTITY = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			ResourceLocation.fromNamespaceAndPath(ExpressCarts.MOD_ID, "minecart"),
			EntityType.Builder.of(ExpressMinecartEntity::new, MobCategory.MISC).noLootTable()
					.sized(0.98F, 0.7F).passengerAttachments(0.1875F).clientTrackingRange(8)
					.build(ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(),
							ResourceLocation.fromNamespaceAndPath(ExpressCarts.MOD_ID, "minecart"))));

	@Override
	public void onInitialize() {
		new ModConfigBuilder<CommandSourceStack, CommandBuildContext>(ExpressCarts.MOD_ID, ExpressCartsConfig.class).build();

		PolymerEntityUtils.registerType(EXPRESS_MINECART_ENTITY);
		PolymerResourcePackUtils.addModAssets(ExpressCarts.MOD_ID);

		ModItems.initialize();

		DispenserBlock.registerBehavior(ModItems.EXPRESS_MINECART, new MinecartDispenseItemBehavior(EXPRESS_MINECART_ENTITY));

		LOGGER.info("Express Carts ready!");
	}

}
