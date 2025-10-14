package egorkhabarov;

import egorkhabarov.command.AutoVillagerTraderCommand;
import egorkhabarov.config.ConfigData;
import egorkhabarov.config.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoVillagerTraderModClient implements ClientModInitializer {
    public static final String MOD_ID = "auto_villager_trader";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static ConfigData CONFIG;

	@Override
	public void onInitializeClient() {
		AutoVillagerTraderModClient.CONFIG = ConfigManager.getConfig();
        AutoVillagerTraderCommand.register();
	}
}