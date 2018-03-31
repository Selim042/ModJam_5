package selim.modjam.packs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import selim.modjam.packs.network.MessageBulkUpdateContainerBackpack;
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
	private static int packetId = 1;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		CapabilityBackpackHandler.register();
		network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		network.registerMessage(MessageBulkUpdateContainerBackpack.Handler.class,
				MessageBulkUpdateContainerBackpack.class, packetId++, Side.CLIENT);
		network.registerMessage(MessageUpdateContainerBackpack.Handler.class,
				MessageUpdateContainerBackpack.class, packetId++, Side.CLIENT);
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
				|| ((ItemArmor) stack.getItem()).armorType != EntityEquipmentSlot.CHEST)
			return;
		LOGGER.info("Attaching capability to " + stack);
		event.addCapability(CAPABILITY_ID, new BackpackHandler(stack));
	}

	@SubscribeEvent
	public void onInteract(PlayerInteractEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		ItemStack pack = ItemStack.EMPTY;
		for (ItemStack stack : player.getArmorInventoryList()) {
			if (stack.hasCapability(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null)) {
				pack = stack;
				break;
			}
		}
		if (pack.isEmpty())
			return;
		player.displayGUIChest(new BackpackHandlerWrapper(pack));
	}

	public class BackpackHandlerWrapper implements IInventory {

		private final ItemStack stack;
		private final IBackpackHandler handler;

		public BackpackHandlerWrapper(ItemStack stack) {
			this.stack = stack;
			if (!stack.hasCapability(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null))
				throw new IllegalArgumentException("ItemStack must have a backpack capability attached");
			this.handler = stack.getCapability(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY,
					null);
		}

		@Override
		public String getName() {
			return "backpack";
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}

		@Override
		public ITextComponent getDisplayName() {
			return new TextComponentString(stack.getDisplayName());
		}

		@Override
		public int getSizeInventory() {
			System.out.println(handler.getSlots());
			return handler.getSlots();
		}

		@Override
		public boolean isEmpty() {
			for (int s = 0; s < handler.getSlots(); s++)
				if (!handler.getStackInSlot(s).isEmpty())
					return false;
			return true;
		}

		@Override
		public ItemStack getStackInSlot(int index) {
			return handler.getStackInSlot(index);
		}

		@Override
		public ItemStack decrStackSize(int index, int count) {
			return handler.extractItem(index, count, false);
		}

		@Override
		public ItemStack removeStackFromSlot(int index) {
			return handler.extractItem(index, handler.getStackInSlot(index).getCount(), false);
		}

		@Override
		public void setInventorySlotContents(int index, ItemStack stack) {
			handler.setStackInSlot(index, stack);
		}

		@Override
		public int getInventoryStackLimit() {
			return 64;
		}

		@Override
		public void markDirty() {}

		@Override
		public boolean isUsableByPlayer(EntityPlayer player) {
			return true;
		}

		@Override
		public void openInventory(EntityPlayer player) {
			// player.displayGUIChest(this);
		}

		@Override
		public void closeInventory(EntityPlayer player) {}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack) {
			return true;
		}

		@Override
		public int getField(int id) {
			return 0;
		}

		@Override
		public void setField(int id, int value) {}

		@Override
		public int getFieldCount() {
			return 0;
		}

		@Override
		public void clear() {
			for (int s = 0; s < handler.getSlots(); s++)
				handler.setStackInSlot(s, ItemStack.EMPTY);
		}
	}

}
