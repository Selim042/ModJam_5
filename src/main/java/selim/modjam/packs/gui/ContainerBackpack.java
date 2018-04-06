package selim.modjam.packs.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import selim.modjam.packs.capabilities.IBackpackHandler;

public class ContainerBackpack extends Container {

	private final IBackpackHandler backpack;
	private final EntityPlayer player;
	private final int numRows;

	public ContainerBackpack(IBackpackHandler backpack, EntityPlayer player) {
		this.backpack = backpack;
		this.player = player;
		InventoryPlayer playerInv = player.inventory;
		this.numRows = backpack.getSlots() / 9;
		int i = (numRows - 4) * 18;
		// Backpack inventory
		for (int y = 0; y < numRows; y++)
			for (int x = 0; x < 9; x++)
				this.addSlotToContainer(
						new SlotItemHandler(backpack, x + y * 9, 8 + x * 18, 18 + y * 18));
		// Player main inventory
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 9; x++)
				this.addSlotToContainer(
						new Slot(playerInv, x + y * 9 + 9, 8 + x * 18, 103 + y * 18 + i));
		// Player hotbar
		for (int x = 0; x < 9; x++)
			this.addSlotToContainer(new Slot(playerInv, x, 8 + x * 18, 161 + i));
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack prevStack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack newStack = slot.getStack();
			prevStack = newStack.copy();
			if (index < this.numRows * 9)
				if (!this.mergeItemStack(newStack, this.numRows * 9, this.inventorySlots.size(), true))
					return ItemStack.EMPTY;
				else if (!this.mergeItemStack(newStack, 0, this.numRows * 9, false))
					return ItemStack.EMPTY;
			if (newStack.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
		}
		return prevStack;
	}

	public IBackpackHandler getBackpack() {
		return this.backpack;
	}

}
