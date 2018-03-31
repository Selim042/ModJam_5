package selim.modjam.packs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ModJamPacks.MODID, name = ModJamPacks.NAME, version = ModJamPacks.VERSION)
public class ModJamPacks {

	public static final String MODID = "selimpacks";
	public static final String NAME = "Selim Backpacks";
	public static final String VERSION = "1.0";
	public static final ResourceLocation CAPABILITY_ID = new ResourceLocation(MODID, "backpack");
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		CapabilityBackpackHandler.register();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {}

	@SubscribeEvent
	public void stackCapAttach(AttachCapabilitiesEvent<ItemStack> event) {
		ItemStack stack = event.getObject();
		if (stack == null || !(stack.getItem() instanceof ItemArmor)
				|| ((ItemArmor) stack.getItem()).armorType != EntityEquipmentSlot.CHEST
				|| stack.getTagCompound() == null
				|| !stack.getTagCompound().getBoolean(CAPABILITY_ID.toString())
				|| stack.hasCapability(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null)
				|| event.getCapabilities().containsKey(CAPABILITY_ID))
			return;
		LOGGER.info("Attaching capability to " + stack);
		event.addCapability(CAPABILITY_ID, new BackpackHandler());
	}

	@SubscribeEvent
	public void onInteract(PlayerInteractEvent event) {
		System.out.println('s');
		EntityPlayer player = event.getEntityPlayer();
		ItemStack pack = null;
		for (ItemStack stack : player.getArmorInventoryList()) {
			if (stack.hasCapability(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null)) {
				pack = stack;
				break;
			}
		}
		if (pack == null)
			return;
		System.out.println("found pack: " + pack);
	}

}
