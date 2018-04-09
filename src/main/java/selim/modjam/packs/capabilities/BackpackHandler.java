package selim.modjam.packs.capabilities;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import selim.modjam.packs.ModConfig;
import selim.modjam.packs.ModJamPacks;
import selim.modjam.packs.items.IBackpackUpgrade;
import selim.modjam.packs.items.ItemCapacityUpgrade;
import selim.modjam.packs.items.ItemEnderUpgrade;

public class BackpackHandler extends ItemStackHandler implements IBackpackHandler,
		// ICapabilitySerializable<NBTTagCompound>
		ICapabilityProvider {

	private ItemStack backpack;
	private final ItemStackHandler contents;
	private final ItemStackHandler upgrades = new ItemStackHandler(9);
	private final List<ItemStackHandler> sizeUpgrades = new LinkedList<ItemStackHandler>();
	private ItemStack enderUpgrade;
	private CombinedInvWrapper wrapper;

	@Override
	protected void onContentsChanged(int slot) {
		super.onContentsChanged(slot);
		saveCapability();
	}

	private void saveCapability() {
		NBTTagCompound nbt = backpack.getTagCompound();
		if (nbt == null)
			nbt = new NBTTagCompound();
		if (ModConfig.VERBOSE)
			ModJamPacks.LOGGER.info("Saving backpack NBT.");
		nbt.setTag(ModJamPacks.MODID + ":backpack_data", serializeNBT());
		backpack.setTagCompound(nbt);
		
	}

	protected BackpackHandler() {
		this(ModConfig.DEFAULT_SIZE);
	}

	public BackpackHandler(ItemStack chestplate) {
		this(ModConfig.getSize(chestplate));
		this.backpack = chestplate;
	}

	public BackpackHandler(ItemStack chestplate, int size) {
		if (ModConfig.hasConfig(chestplate))
			this.contents = new ItemStackHandler(ModConfig.getSize(chestplate));
		else
			this.contents = new ItemStackHandler(size);
		this.initUpgrades();
	}

	private BackpackHandler(int size) {
		this.contents = new ItemStackHandler(size);
		this.initUpgrades();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		boolean hasCap = false;
		if (this.backpack == null)
			hasCap = true;
		else {
			NBTTagCompound nbt = this.backpack.getTagCompound();
			if (nbt != null)
				hasCap = nbt.hasKey(ModJamPacks.MODID + ":backpack");
		}
		if (hasCap)
			return capability.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
					|| capability.equals(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY);
		return false;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (!hasCapability(capability, facing))
			return null;
		if (capability.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY))
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(contents);
		if (capability.equals(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY))
			return CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY.cast(this);
		return null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound upperNbt = super.serializeNBT();
		upperNbt.setTag("contents", contents.serializeNBT());
		upperNbt.setTag("upgrades", upgrades.serializeNBT());
		return upperNbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.contents.deserializeNBT(nbt.getCompoundTag("contents"));
		this.upgrades.deserializeNBT(nbt.getCompoundTag("upgrades"));
		super.deserializeNBT(nbt);
	}

	@Override
	public void setSize(int size) {
		this.setSize(null, size);
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		this.setStackInSlot(null, slot, stack);
	}

	@Override
	public int getSlots() {
		return this.getSlots(null);
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return this.getStackInSlot(null, slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return this.insertItem(null, slot, stack, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return this.extractItem(null, slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return this.getSlotLimit(null, slot);
	}

	// @Override
	// protected int getStackLimit(int slot, ItemStack stack) {
	// return this.getStackLimit(null, slot, stack);
	// }

	// Player aware versions (vanilla ender chest)
	@Override
	public void setSize(EntityPlayer player, int size) {}

	@Override
	public void setStackInSlot(EntityPlayer player, int slot, ItemStack stack) {
		if (this.wrapper == null)
			this.updateSizeUpgrades();
		IItemHandlerModifiable handler = getEnderInventory(player);
		if (handler != null)
			handler.setStackInSlot(slot, getTrueInsert(stack));
		else
			wrapper.setStackInSlot(slot, getTrueInsert(stack));
		this.onContentsChanged(slot);
	}

	@Override
	public int getSlots(EntityPlayer player) {
		if (this.wrapper == null)
			this.updateSizeUpgrades();
		IItemHandlerModifiable handler = getEnderInventory(player);
		if (handler != null)
			return handler.getSlots();
		else
			return wrapper.getSlots();
	}

	@Override
	public ItemStack getStackInSlot(EntityPlayer player, int slot) {
		if (this.wrapper == null)
			this.updateSizeUpgrades();
		IItemHandlerModifiable handler = getEnderInventory(player);
		if (handler != null)
			return handler.getStackInSlot(slot);
		else
			return wrapper.getStackInSlot(slot);
	}

	@Override
	public ItemStack insertItem(EntityPlayer player, int slot, ItemStack stack, boolean simulate) {
		if (this.wrapper == null)
			this.updateSizeUpgrades();
		IItemHandlerModifiable handler = getEnderInventory(player);
		ItemStack toReturn;
		if (handler != null)
			toReturn = handler.insertItem(slot, getTrueInsert(stack), simulate);
		else
			toReturn = wrapper.insertItem(slot, getTrueInsert(stack), simulate);
		this.onContentsChanged(slot);
		return toReturn;
	}

	@Override
	public ItemStack extractItem(EntityPlayer player, int slot, int amount, boolean simulate) {
		if (this.wrapper == null)
			this.updateSizeUpgrades();
		IItemHandlerModifiable handler = getEnderInventory(player);
		ItemStack toReturn;
		if (handler != null)
			toReturn = handler.extractItem(slot, amount, simulate);
		else
			toReturn = wrapper.extractItem(slot, amount, simulate);
		this.onContentsChanged(slot);
		return toReturn;
	}

	@Override
	public int getSlotLimit(EntityPlayer player, int slot) {
		if (this.wrapper == null)
			this.updateSizeUpgrades();
		IItemHandlerModifiable handler = getEnderInventory(player);
		if (handler != null)
			return handler.getSlotLimit(slot);
		else
			return wrapper.getSlotLimit(slot);
	}

	// @Override
	// public int getStackLimit(EntityPlayer player, int slot, ItemStack stack)
	// {
	// if (this.wrapper == null)
	// this.updateSizeUpgrades();
	// return wrapper.getStackLimit(slot, stack);
	// }

	@Override
	public void setEnderUpgrade(ItemStack enderUpgrade) {
		if (enderUpgrade != null && enderUpgrade.getItem() instanceof ItemEnderUpgrade)
			this.enderUpgrade = enderUpgrade;
	}

	// @Override
	// public ItemStack getEnderUpgrade() {
	// return this.enderUpgrade;
	// }

	private IItemHandlerModifiable getEnderInventory(EntityPlayer player) {
		if (enderUpgrade != null && enderUpgrade.getItem() instanceof ItemEnderUpgrade)
			return ((ItemEnderUpgrade) enderUpgrade.getItem()).getEnderInventory(player, enderUpgrade);
		return null;
	}

	private ItemStack getTrueInsert(ItemStack stack) {
		for (int s = 0; s < upgrades.getSlots(); s++) {
			ItemStack upgrade = upgrades.getStackInSlot(s);
			if (!(upgrade.getItem() instanceof IBackpackUpgrade))
				continue;
			ItemStack toInsert = ((IBackpackUpgrade) upgrade.getItem()).onItemAdd(this, stack);
			if (upgrade.equals(toInsert))
				continue;
			if (toInsert != null)
				return toInsert;
		}
		return stack;
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
	public ItemStackHandler getUpgradeHandler() {
		return this.upgrades;
	}

	@Override
	public void setUpgradeStackInSlot(int slot, ItemStack stack) {
		ItemStack prevStack = getUpgradeStackInSlot(slot);
		if (prevStack.getItem() instanceof IBackpackUpgrade)
			((IBackpackUpgrade) prevStack.getItem()).onUpgradeRemoved(this, prevStack);
		this.upgrades.setStackInSlot(slot, stack);
		if (stack.getItem() instanceof IBackpackUpgrade)
			((IBackpackUpgrade) stack.getItem()).onUpgradeAdded(this, stack);
		this.updateSizeUpgrades();
		saveCapability();
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
		saveCapability();
		return toReturn;
	}

	@Override
	public ItemStack extractUpgradeItem(int slot, int amount, boolean simulate) {
		ItemStack toReturn = this.upgrades.extractItem(slot, amount, simulate);
		if (toReturn.getItem() instanceof IBackpackUpgrade)
			((IBackpackUpgrade) toReturn.getItem()).onUpgradeRemoved(this, toReturn);
		this.updateSizeUpgrades();
		saveCapability();
		return toReturn;
	}

	@Override
	public int getUpgradeSlotLimit(int slot) {
		return this.upgrades.getSlotLimit(slot);
	}

	private boolean init = false;

	private void initUpgrades() {
		if (init)
			return;
		init = true;
		for (int s = 0; s < this.upgrades.getSlots(); s++) {
			ItemStack stack = this.upgrades.getStackInSlot(s);
			if (stack.getItem() instanceof IBackpackUpgrade)
				((IBackpackUpgrade) stack.getItem()).onUpgradeAdded(this, stack);
		}
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
		if (ModConfig.VERBOSE)
			ModJamPacks.LOGGER
					.info("Constructing new inventory wrapper with " + handlers.length + " handlers");
		this.wrapper = new CombinedInvWrapper(handlers);
	}

}
