package expresscarts;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;

public class ExpressCarts implements ModInitializer {
	public static final String MOD_ID = "expresscarts";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final EntityType<ExpressMinecartEntity> EXPRESS_MINECART_ENTITY = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of(ExpressCarts.MOD_ID, "minecart"),
			EntityType.Builder.create(ExpressMinecartEntity::new, SpawnGroup.MISC).dropsNothing()
					.dimensions(0.98F, 0.7F).passengerAttachments(0.1875F).maxTrackingRange(8)
					.build(RegistryKey.of(Registries.ENTITY_TYPE.getKey(),
							Identifier.of(ExpressCarts.MOD_ID, "minecart"))));

	@Override
	public void onInitialize() {
		PolymerEntityUtils.registerType(EXPRESS_MINECART_ENTITY);

		ModItems.initialize();

		LOGGER.info("Express Carts ready!");
	}

}
