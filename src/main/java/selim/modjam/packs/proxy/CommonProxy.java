package selim.modjam.packs.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import selim.modjam.packs.items.ItemCapacityUpgrade;
import selim.modjam.packs.items.ItemCollectionUpgrade;
import selim.modjam.packs.items.ItemEnderUpgrade;
import selim.modjam.packs.items.ItemSmeltingUpgrade;
import selim.modjam.packs.network.CapabilityContainerListenerManager;

@Mod.EventBusSubscriber
public class CommonProxy {

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		// TODO: Figure out why this is the only nonworking backpack
		// event.getRegistry().register(new ItemBackpack());
		// TODO: Figure out why it isn't working on multiplayer
//		event.getRegistry().register(new ItemSmeltingUpgrade());
		event.getRegistry().register(new ItemCollectionUpgrade());
		event.getRegistry().register(new ItemCapacityUpgrade());
		event.getRegistry().register(new ItemEnderUpgrade());
	}

	public void registerKeybinds() {}

	public void registerEventListeners() {
		// MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(ItemCollectionUpgrade.class);
//		MinecraftForge.EVENT_BUS.register(CapabilityContainerListenerManager.class);
	}

	public IThreadListener getThreadListener(final MessageContext context) {
		if (context.side.isServer())
			return context.getServerHandler().player.mcServer;
		else
			throw new IllegalArgumentException(
					"Tried to get the IThreadListener from a client-side MessageContext on the dedicated server");
	}

	public EntityPlayer getPlayer(final MessageContext context) {
		if (context.side.isServer())
			return context.getServerHandler().player;
		else
			throw new IllegalArgumentException(
					"Tried to get the player from a client-side MessageContext on the dedicated server");
	}

	public void preInit() {}

	public void init() {}

	public void postInit() {}

}
