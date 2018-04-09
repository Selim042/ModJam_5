package selim.modjam.packs.proxy;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import selim.modjam.packs.ModConfig;
import selim.modjam.packs.ModJamPacks;
import selim.modjam.packs.PacksItems;
import selim.modjam.packs.capabilities.CapabilityBackpackHandler;
import selim.modjam.packs.items.ItemEnderUpgrade;
import selim.modjam.packs.network.MessageOpenBackpack;
import selim.modjam.packs.network.MessageOpenUpgrades;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

	public static final KeyBinding openBackpack = new KeyBinding(
			"key." + ModJamPacks.MODID + ":open_backpack", Keyboard.KEY_G,
			"key." + ModJamPacks.MODID + ".category");
	public static final KeyBinding openUpgrades = new KeyBinding(
			"key." + ModJamPacks.MODID + ":open_upgrades", Keyboard.KEY_H,
			"key." + ModJamPacks.MODID + ".category");

	@Override
	public void registerKeybinds() {
		ClientRegistry.registerKeyBinding(openBackpack);
		ClientRegistry.registerKeyBinding(openUpgrades);
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		// MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(ModConfig.EventHandler.class);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public static void onEvent(KeyInputEvent event) {
		if (openBackpack.isPressed()) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			if (stack.isEmpty()
					|| !stack.hasCapability(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null))
				return;
			ModJamPacks.network.sendToServer(new MessageOpenBackpack());
		}
		if (openUpgrades.isPressed()) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			if (stack.isEmpty()
					|| !stack.hasCapability(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null))
				return;
			ModJamPacks.network.sendToServer(new MessageOpenUpgrades());
		}
	}

	@Override
	public IThreadListener getThreadListener(final MessageContext context) {
		if (context.side.isClient())
			return Minecraft.getMinecraft();
		else
			return context.getServerHandler().player.mcServer;
	}

	@Override
	public EntityPlayer getPlayer(final MessageContext context) {
		if (context == null || context.side.isClient())
			return Minecraft.getMinecraft().player;
		else
			return context.getServerHandler().player;
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		// registerModel(PacksItems.BACKPACK);
		registerModel(PacksItems.SMELTING_UPGRADE);
		registerModel(PacksItems.COLLECTION_UPGRADE);
		// registerModel(PacksItems.CAPACITY_UPGRADE);
		registerModel(PacksItems.FILTERED_COLLECTION_UPGRADE);

		ModelLoader.setCustomMeshDefinition(PacksItems.CAPACITY_UPGRADE, new ItemMeshDefinition() {

			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				NBTTagCompound nbt = stack.getTagCompound();
				if (nbt == null || !nbt.getBoolean("3d"))
					return new ModelResourceLocation(
							new ResourceLocation(ModJamPacks.MODID, "capacity_upgrade"), "inventory");
				return new ModelResourceLocation(
						new ResourceLocation(ModJamPacks.MODID, "3_d_capacity_upgrade"), "inventory");
			}

		});
		ModelLoader.registerItemVariants(PacksItems.CAPACITY_UPGRADE,
				new ModelResourceLocation(new ResourceLocation(ModJamPacks.MODID, "capacity_upgrade"),
						"inventory"),
				new ModelResourceLocation(
						new ResourceLocation(ModJamPacks.MODID, "3_d_capacity_upgrade"), "inventory"));

		ModelLoader.setCustomMeshDefinition(PacksItems.ENDER_UPGRADE,
				new ItemEnderUpgrade.EnderMeshDefinition());
		ModelLoader.registerItemVariants(PacksItems.ENDER_UPGRADE,
				new ModelResourceLocation(
						new ResourceLocation(ModJamPacks.MODID, "ender_upgrade_unowned"), "inventory"),
				new ModelResourceLocation(new ResourceLocation(ModJamPacks.MODID, "ender_upgrade_owned"),
						"inventory"),
				new ModelResourceLocation(new ResourceLocation(ModJamPacks.MODID, "ender_upgrade"),
						"inventory"));
	}

	private static void registerModel(Item item) {
		if (item == null)
			return;
		ModelLoader.setCustomModelResourceLocation(item, 0,
				new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

	@Override
	public void preInit() {}

	@Override
	public void init() {
		ItemColors colors = Minecraft.getMinecraft().getItemColors();
		colors.registerItemColorHandler(new ItemEnderUpgrade.EnderUpgradeItemColor(),
				PacksItems.ENDER_UPGRADE);
	}

	@Override
	public void postInit() {}

}
