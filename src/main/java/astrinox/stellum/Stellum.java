package astrinox.stellum;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import astrinox.stellum.command.StellumDebugCommand;

public class Stellum implements ModInitializer {
	public static final String MOD_ID = "stellum";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("✨");

		CommandRegistrationCallback.EVENT.register(StellumDebugCommand::register);
	}
}