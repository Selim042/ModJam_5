package selim.modjam.packs.wrappers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import selim.modjam.packs.ModJamPacks;
import selim.modjam.packs.capabilities.CapabilityBackpackHandler;
import selim.modjam.packs.capabilities.IBackpackHandler;

public class BackpackHandlerWrapper implements IInventory {

	private final ItemStack stack;
	private final EntityPlayer player;
	private final IBackpackHandler handler;

	public BackpackHandlerWrapper(ItemStack stack, EntityPlayer player) {
		this.stack = stack;
		this.player = player;
		if (!stack.hasCapability(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null))
			throw new IllegalArgumentException("ItemStack must have a backpack capability attached");
		this.handler = stack.getCapability(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null);
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
		return new TextComponentString(stack.getDisplayName() + " ").appendSibling(
				new TextComponentTranslation("misc." + ModJamPacks.MODID + ":backpack_inv"));
	}

	@Override
	public int getSizeInventory() {
		return handler.getSlots(player);
	}

	@Override
	public boolean isEmpty() {
		for (int s = 0; s < handler.getSlots(); s++)
			if (!handler.getStackInSlot(player, s).isEmpty())
				return false;
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return handler.getStackInSlot(player, index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return handler.extractItem(player, index, count, false);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return handler.extractItem(player, index, handler.getStackInSlot(index).getCount(), false);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		handler.setStackInSlot(player, index, stack);
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
	public void openInventory(EntityPlayer player) {}

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