package selim.modjam.packs.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import selim.modjam.packs.capabilities.CapabilityBackpackHandler;
import selim.modjam.packs.capabilities.IBackpackHandler;

public class GuiHandler implements IGuiHandler {

	public static final int BACKPACK = 0;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case BACKPACK:
			return new ContainerBackpack(getBackpack(player), player);
		default:
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case BACKPACK:
			return new GuiBackpack(new ContainerBackpack(getBackpack(player), player), player);
		default:
			return null;
		}
	}

	private IBackpackHandler getBackpack(EntityPlayer player) {
		ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if (stack == null || stack.isEmpty()
				|| !stack.hasCapability(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null))
			return null;
		return stack.getCapability(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null);
	}

}
