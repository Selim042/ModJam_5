package selim.modjam.packs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import selim.modjam.packs.capabilities.BackpackHandler;
import selim.modjam.packs.capabilities.CapabilityBackpackHandler;
import selim.modjam.packs.compat.EnderStorageHelper;
import selim.modjam.packs.gui.GuiHandler;
import selim.modjam.packs.items.ItemBackpack;
import selim.modjam.packs.network.MessageBulkUpdateContainerBackpack;
import selim.modjam.packs.network.MessageOpenBackpack;
import selim.modjam.packs.network.MessageOpenUpgrades;
import selim.modjam.packs.network.MessageUpdateContainerBackpack;
import selim.modjam.packs.proxy.CommonProxy;

@Mod(modid = ModJamPacks.MODID, name = ModJamPacks.NAME, version = ModJamPacks.VERSION,
		dependencies = "after:" + EnderStorageHelper.ID,
		updateJSON = "http://myles-selim.us/modInfo/selimBackpacks.json")
public class ModJamPacks {

	public static final String MODID = "selimpacks";
	public static final String NAME = "Selim Backpacks";
	public static final String VERSION = "1.1.2";
	public static final ResourceLocation CAPABILITY_ID = new ResourceLocation(MODID, "backpack");
	@Mod.Instance(value = MODID)
	public static ModJamPacks instance;
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static SimpleNetworkWrapper network;
	@SidedProxy(clientSide = "selim.modjam.packs.proxy.ClientProxy",
			serverSide = "selim.modjam.packs.proxy.CommonProxy")
	public static CommonProxy proxy;
	public static final BackpackTab CREATIVE_TAB = new BackpackTab();
	private static int packetId = 1;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		proxy.registerEventListeners();

		CapabilityBackpackHandler.register();
		network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		network.registerMessage(MessageBulkUpdateContainerBackpack.Handler.class,
				MessageBulkUpdateContainerBackpack.class, packetId++, Side.CLIENT);
		network.registerMessage(MessageUpdateContainerBackpack.Handler.class,
				MessageUpdateContainerBackpack.class, packetId++, Side.CLIENT);
		network.registerMessage(MessageOpenBackpack.Handler.class, MessageOpenBackpack.class, packetId++,
				Side.SERVER);
		network.registerMessage(MessageOpenUpgrades.Handler.class, MessageOpenUpgrades.class, packetId++,
				Side.SERVER);
		proxy.registerKeybinds();

		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

		// Add all backpacks
		NBTTagCompound innerNbt = new NBTTagCompound();
		innerNbt.setBoolean(MODID + ":backpack", true);
		for (Item item : ForgeRegistries.ITEMS.getValuesCollection()) {
			if (item instanceof ItemBackpack || !(item instanceof ItemArmor)
					|| ((ItemArmor) item).armorType != EntityEquipmentSlot.CHEST
					|| ModConfig.getSize(item) == 0)
				continue;
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("id", item.getRegistryName().toString());
			nbt.setByte("Count", (byte) 1);
			nbt.setTag("tag", innerNbt);
			ItemStack stack = new ItemStack(nbt);
			// TODO: Make this recipe JSON compatible
			GameRegistry.addShapelessRecipe(
					new ResourceLocation(MODID, item.getRegistryName().getResourcePath() + "_backpack"),
					new ResourceLocation(MODID, "backpack"), stack, Ingredient.fromItem(item),
					CraftingHelper.getIngredient("chestWood"));
			if (ModConfig.VERBOSE)
				LOGGER.info("Adding recipe for backpack form of " + stack + "("
						+ stack.getItem().getRegistryName() + ")");
			BackpackTab.addBackpack(stack);
		}

		proxy.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if (ModConfig.VERBOSE) {
			ForgeVersion.CheckResult result = ForgeVersion
					.getResult(Loader.instance().activeModContainer());
			ForgeVersion.Status status = result.status;
			LOGGER.info(NAME + " is " + status);
			if (status == ForgeVersion.Status.OUTDATED || status == ForgeVersion.Status.BETA_OUTDATED)
				LOGGER.info("Please update to " + result.target + " before reporting any issues.");
		}

		proxy.postInit();
	}

	@SubscribeEvent
	public void stackCapAttach(AttachCapabilitiesEvent<ItemStack> event) {
		ItemStack stack = event.getObject();
		if (stack == null || !(stack.getItem() instanceof ItemArmor)
				|| ((ItemArmor) stack.getItem()).armorType != EntityEquipmentSlot.CHEST
				|| stack.hasCapability(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null))
			return;
		if (ModConfig.VERBOSE)
			LOGGER.info(
					"Attaching capability to " + stack + "(" + stack.getItem().getRegistryName() + ")");
		BackpackHandler cap = new BackpackHandler(stack);
		event.addCapability(CAPABILITY_ID, cap);
		if (cap.hasCapability(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null)) {
			NBTTagCompound capNbt = stack.getSubCompound(ModJamPacks.MODID + ":backpack");
			if (capNbt != null)
				cap.deserializeNBT(capNbt);
		}

	}

	// @SubscribeEvent
	// public void onInteract(PlayerInteractEvent event) {
	// EntityPlayer player = event.getEntityPlayer();
	// ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
	// if (stack.isEmpty()
	// ||
	// !stack.hasCapability(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY,
	// null))
	// return;
	// player.displayGUIChest(new BackpackHandlerWrapper(stack));
	// }

	// TODO: quadraxis - Today at 11:25 PM
	// also use a client-side event handler, don't just stick @SideOnly on the
	// method
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		// List<String> nbtList = new ArrayList<String>();
		// NBTTagCompound capNbt =
		// ReflectionHelper.getPrivateValue(ItemStack.class, stack, "capNBT");
		// NBTUtils.nbtToStringList(nbtList, capNbt);
		// event.getToolTip().addAll(nbtList);
		if (stack.hasCapability(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null)
				|| (stack.getTagCompound() != null
						&& stack.getTagCompound().getBoolean(MODID + ":backpack")))
			event.getToolTip().add(I18n.format("misc." + MODID + ":backpack_tooltip"));
		// for (int oreId : OreDictionary.getOreIDs(stack))
		// event.getToolTip().add(" - " + OreDictionary.getOreName(oreId));
	}

}
