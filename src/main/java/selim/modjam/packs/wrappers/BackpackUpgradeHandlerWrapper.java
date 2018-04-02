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

public class BackpackUpgradeHandlerWrapper implements IInventory {

	private final ItemStack stack;
	private final IBackpackHandler handler;

	public BackpackUpgradeHandlerWrapper(ItemStack stack) {
		this.stack = stack;
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
				new TextComponentTranslation("misc." + ModJamPacks.MODID + ":upgrade_inv"));
	}

	@Override
	public int getSizeInventory() {
		return handler.getUpgradeSlots();
	}

	@Override
	public boolean isEmpty() {
		for (int s = 0; s < handler.getUpgradeSlots(); s++)
			if (!handler.getUpgradeStackInSlot(s).isEmpty())
				return false;
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return handler.getUpgradeStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return handler.extractUpgradeItem(index, count, false);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return handler.extractUpgradeItem(index, handler.getStackInSlot(index).getCount(), false);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		handler.setUpgradeStackInSlot(index, stack);
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
			handler.setUpgradeStackInSlot(s, ItemStack.EMPTY);
	}
}