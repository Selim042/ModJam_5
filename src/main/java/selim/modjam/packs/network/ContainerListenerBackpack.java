package selim.modjam.packs.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import selim.modjam.packs.capabilities.CapabilityBackpackHandler;
import selim.modjam.packs.capabilities.IBackpackHandler;

public class ContainerListenerBackpack extends CapabilityContainerListener<IBackpackHandler> {

	public ContainerListenerBackpack(final EntityPlayerMP player) {
		super(player, CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null);
	}

	@Override
	protected boolean shouldSyncItem(final ItemStack stack) {
		return stack.getItem() instanceof ItemArmor
				&& ((ItemArmor) stack.getItem()).armorType == EntityEquipmentSlot.CHEST;
	}

	@Override
	protected MessageBulkUpdateContainerBackpack createBulkUpdateMessage(final int windowID,
			final NonNullList<ItemStack> items) {
		return new MessageBulkUpdateContainerBackpack(windowID, items);
	}

	@Override
	protected MessageUpdateContainerBackpack createSingleUpdateMessage(final int windowID,
			final int slotNumber, final IBackpackHandler fluidHandlerItem) {
		return new MessageUpdateContainerBackpack(windowID, slotNumber, fluidHandlerItem);
	}

}
