package selim.modjam.packs.capabilities;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import selim.modjam.packs.ModConfig;
import selim.modjam.packs.items.ItemCapacityUpgrade;

public class BackpackHandler extends ItemStackHandler implements IBackpackHandler {

	private final ItemStackHandler contents;
	private final ItemStackHandler upgrades = new ItemStackHandler(9);
	private final List<ItemStackHandler> sizeUpgrades = new LinkedList<ItemStackHandler>();
	private CombinedInvWrapper wrapper;

	protected BackpackHandler() {
		this(ModConfig.DEFAULT_SIZE);
	}

	public BackpackHandler(ItemStack chestplate) {
		this(ModConfig.getSize(chestplate));
	}

	private BackpackHandler(int size) {
		this.contents = new ItemStackHandler(size);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
				|| capability.equals(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY))
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(contents);
		if (capability.equals(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY))
			return CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY.cast(this);
		return null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound upperNbt = super.serializeNBT();
		upperNbt.setTag("upgrades", upgrades.serializeNBT());
		return upperNbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		NBTTagCompound upgrades = nbt.getCompoundTag("upgrades");
		this.upgrades.deserializeNBT(upgrades);
		super.deserializeNBT(nbt);
	}

	@Override
	public void setSize(int size) {}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if (this.wrapper == null)
			this.updateSizeUpgrades();
		wrapper.setStackInSlot(slot, stack);
	}

	@Override
	public int getSlots() {
		if (this.wrapper == null)
			this.updateSizeUpgrades();
		return wrapper.getSlots();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if (this.wrapper == null)
			this.updateSizeUpgrades();
		return wrapper.getStackInSlot(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (this.wrapper == null)
			this.updateSizeUpgrades();
		return wrapper.insertItem(slot, stack, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (this.wrapper == null)
			this.updateSizeUpgrades();
		return wrapper.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		if (this.wrapper == null)
			this.updateSizeUpgrades();
		return wrapper.getSlotLimit(slot);
	}

	// Internal inv
	public int getInternalSlots() {
		return contents.getSlots();
	}

	public ItemStack getInternalStackInSlot(int slot) {
		return contents.getStackInSlot(slot);
	}

	public ItemStack extractInternalItem(int slot, int amount, boolean simulate) {
		return contents.extractItem(slot, amount, simulate);
	}

	public int getInternalSlotLimit(int slot) {
		return contents.getSlotLimit(slot);
	}

	// Upgrade methods
	@Override
	public void setUpgradeStackInSlot(int slot, ItemStack stack) {
		this.upgrades.setStackInSlot(slot, stack);
		this.updateSizeUpgrades();
	}

	@Override
	public int getUpgradeSlots() {
		return this.upgrades.getSlots();
	}

	@Override
	public ItemStack getUpgradeStackInSlot(int slot) {
		return this.upgrades.getStackInSlot(slot);
	}

	@Override
	public ItemStack insertUpgradeItem(int slot, ItemStack stack, boolean simulate) {
		ItemStack toReturn = this.upgrades.insertItem(slot, stack, simulate);
		this.updateSizeUpgrades();
		return toReturn;
	}

	@Override
	public ItemStack extractUpgradeItem(int slot, int amount, boolean simulate) {
		ItemStack toReturn = this.upgrades.extractItem(slot, amount, simulate);
		this.updateSizeUpgrades();
		return toReturn;
	}

	@Override
	public int getUpgradeSlotLimit(int slot) {
		return this.upgrades.getSlotLimit(slot);
	}

	private void updateSizeUpgrades() {
		this.sizeUpgrades.clear();
		for (int s = 0; s < this.upgrades.getSlots(); s++) {
			ItemStack stack = this.upgrades.getStackInSlot(s);
			if (stack.getItem() instanceof ItemCapacityUpgrade) {
				IItemHandler handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
						null);
				if (handler instanceof ItemStackHandler)
					this.sizeUpgrades.add((ItemStackHandler) handler);
			}
		}
		ItemStackHandler[] handlers = new ItemStackHandler[this.sizeUpgrades.size() + 1];
		handlers[0] = this.contents;
		for (int i = 0; i < this.sizeUpgrades.size(); i++)
			handlers[i + 1] = this.sizeUpgrades.get(i);
		System.out.println("constructing new wrapper with " + handlers.length + " handlers");
		this.wrapper = new CombinedInvWrapper(handlers);
	}

}
