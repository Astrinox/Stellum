package astrinox.stellum;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import astrinox.stellum.command.StellumDebugCommand;
import astrinox.stellum.resource.StellumResourceReloadListener;

public class Stellum implements ModInitializer {
	public static final String MOD_ID = "stellum";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("✨");

		CommandRegistrationCallback.EVENT.register(StellumDebugCommand::register);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new StellumResourceReloadListener());
	}
}