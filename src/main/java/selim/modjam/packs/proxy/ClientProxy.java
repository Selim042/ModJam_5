package selim.modjam.packs.proxy;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import selim.modjam.packs.CapabilityBackpackHandler;
import selim.modjam.packs.ModJamPacks;
import selim.modjam.packs.network.MessageOpenBackpack;

@Mod.EventBusSubscriber
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

}
