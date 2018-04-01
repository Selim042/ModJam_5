package selim.modjam.packs;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class BackpackHandler extends ItemStackHandler implements IBackpackHandler {

	private final ItemStackHandler upgrades = new ItemStackHandler(9);

	protected BackpackHandler() {
		super(ModConfig.DEFAULT_SIZE);
	}

	public BackpackHandler(ItemStack chestplate) {
		super(ModConfig.getSize(chestplate));
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
				|| capability.equals(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY))
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this);
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
	public void setUpgradeStackInSlot(int slot, ItemStack stack) {
		this.upgrades.setStackInSlot(slot, stack);
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
		return this.upgrades.insertItem(slot, stack, simulate);
	}

	@Override
	public ItemStack extractUpgradeItem(int slot, int amount, boolean simulate) {
		return this.upgrades.extractItem(slot, amount, simulate);
	}

	@Override
	public int getUpgradeSlotLimit(int slot) {
		return this.upgrades.getSlotLimit(slot);
	}

}
