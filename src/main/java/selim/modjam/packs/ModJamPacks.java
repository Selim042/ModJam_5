package selim.modjam.packs;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
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
import selim.modjam.packs.capabilities.BackpackHandler;
import selim.modjam.packs.capabilities.CapabilityBackpackHandler;
import selim.modjam.packs.capabilities.IBackpackHandler;
import selim.modjam.packs.network.MessageBulkUpdateContainerBackpack;
import selim.modjam.packs.network.MessageOpenBackpack;
import selim.modjam.packs.network.MessageOpenUpgrades;
import selim.modjam.packs.network.MessageUpdateContainerBackpack;
import selim.modjam.packs.proxy.CommonProxy;

@Mod(modid = ModJamPacks.MODID, name = ModJamPacks.NAME, version = ModJamPacks.VERSION)
public class ModJamPacks {

	public static final String MODID = "selimpacks";
	public static final String NAME = "Selim Backpacks";
	public static final String VERSION = "1.0";
	public static final ResourceLocation CAPABILITY_ID = new ResourceLocation(MODID, "backpack");
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static SimpleNetworkWrapper network;
	@SidedProxy(clientSide = "selim.modjam.packs.proxy.ClientProxy",
			serverSide = "selim.modjam.packs.proxy.CommonProxy")
	public static CommonProxy proxy;
	public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(MODID) {

		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(PacksItems.BACKPACK);
		}

	};
	private static int packetId = 1;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
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
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);

		// Add all backpack recipes
		NBTTagCompound innerNbt = new NBTTagCompound();
		innerNbt.setBoolean(MODID + ":backpack", true);
		for (Item item : ForgeRegistries.ITEMS.getValuesCollection()) {
			if (!(item instanceof ItemArmor) || ((ItemArmor) item).armorType != EntityEquipmentSlot.CHEST
					|| ModConfig.getSize(item) == 0)
				continue;
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("id", item.getRegistryName().toString());
			nbt.setByte("Count", (byte) 1);
			nbt.setTag("tag", innerNbt);
			GameRegistry.addShapelessRecipe(
					new ResourceLocation(MODID, item.getRegistryName().getResourcePath() + "_backpack"),
					new ResourceLocation(MODID, "backpack"), new ItemStack(nbt),
					Ingredient.fromItem(item), CraftingHelper.getIngredient("chestWood"));
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {}

	@SubscribeEvent
	public void stackCapAttach(AttachCapabilitiesEvent<ItemStack> event) {
		ItemStack stack = event.getObject();
		if (stack == null || !(stack.getItem() instanceof ItemArmor)
				|| ((ItemArmor) stack.getItem()).armorType != EntityEquipmentSlot.CHEST
				|| stack.getTagCompound() == null
				|| !stack.getTagCompound().getBoolean(MODID + ":backpack"))
			return;
		System.out.println("Attaching to: " + stack);
		event.addCapability(CAPABILITY_ID, new BackpackHandler(stack));
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

	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		List<String> nbtList = new ArrayList<String>();
		NBTUtils.nbtToStringList(nbtList, stack.getTagCompound());
		event.getToolTip().addAll(nbtList);
		if (stack.hasCapability(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null)
				|| (stack.getTagCompound() != null
						&& stack.getTagCompound().getBoolean(MODID + ":backpack"))) {
			IBackpackHandler backpack = stack
					.getCapability(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null);
			event.getToolTip().add(I18n.format("misc." + MODID + ":backpack_tooltip"));
		}
	}

}
