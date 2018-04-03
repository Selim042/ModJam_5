package selim.modjam.packs.proxy;

import java.lang.reflect.Field;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
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
import selim.modjam.packs.network.MessageOpenBackpack;
import selim.modjam.packs.network.MessageOpenUpgrades;

@SideOnly(Side.CLIENT)
// @Mod.EventBusSubscriber(Side.CLIENT)
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
		MinecraftForge.EVENT_BUS.register(this);
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
		Class<PacksItems> clazz = PacksItems.class;
		Field[] fields = clazz.getDeclaredFields();
		try {
			for (Field f : fields) {
				Object obj = f.get(null);
				if (obj == null || obj == Items.AIR)
					continue;
				if (obj instanceof Item) {
					registerModel((Item) obj);
				} else
					System.out.println("Failed to register: " + f.getName());
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			ModJamPacks.LOGGER.error(
					"An " + e.getClass().getName() + " was thrown when attempting to load Item models.");
			e.printStackTrace();
		}
	}

	private static void registerModel(Item item) {
		if (item == null)
			return;
		ModelLoader.setCustomModelResourceLocation(item, 0,
				new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

}
