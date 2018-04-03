package selim.modjam.packs;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Config(modid = ModJamPacks.MODID)
@Config.LangKey(ModJamPacks.MODID + ":config.title")
public class ModConfig {

	@Config.Comment("Default backpack size for any chestplate that doesn't have a defined size.")
	public static int DEFAULT_SIZE = 18;

	@Config.Comment({ "Backpack sizes for all chestplates.", "Values must be a multiple of 9.",
			"Set the value to 0 to disable backpacks for the given chestplate." })
	public static final Map<String, Integer> PACK_SIZES = new HashMap<>();

	static {
		PACK_SIZES.put(ModJamPacks.MODID + ":backpack", 18);
		PACK_SIZES.put("minecraft:leather_chesplate", 27);
		PACK_SIZES.put("minecraft:chain_chesplate", 0);
		PACK_SIZES.put("minecraft:iron_chesplate", 9);
		PACK_SIZES.put("minecraft:gold_chesplate", 18);
		PACK_SIZES.put("minecraft:diamond_chesplate", 36);
	}

	public static int getSize(ItemStack stack) {
		if (stack == null || stack.isEmpty())
			return 0;
		return getSize(stack.getItem());
	}

	public static int getSize(Item item) {
		if (DEFAULT_SIZE % 9 != 0)
			DEFAULT_SIZE = 18;
		String id = item.getRegistryName().toString();
		if (!PACK_SIZES.containsKey(id))
			return DEFAULT_SIZE;
		int size = PACK_SIZES.get(id);
		if (size % 9 != 0)
			return DEFAULT_SIZE;
		return size;
	}

//	@Mod.EventBusSubscriber(modid = ModJamPacks.MODID, value = Side.CLIENT)
	public static class EventHandler {

		/**
		 * Inject the new values and save to the config file when the config has
		 * been changed from the GUI.
		 *
		 * @param event
		 *            The event
		 */
		@SubscribeEvent
		public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(ModJamPacks.MODID))
				ConfigManager.sync(ModJamPacks.MODID, Config.Type.INSTANCE);
		}
	}

}