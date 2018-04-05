package selim.modjam.packs.capabilities;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public interface IBackpackHandler extends IItemHandlerModifiable {

	public void setSize(EntityPlayer player, int size);

	public void setStackInSlot(EntityPlayer player, int slot, ItemStack stack);

	public int getSlots(EntityPlayer player);

	public ItemStack getStackInSlot(EntityPlayer player, int slot);

	ItemStack insertItem(EntityPlayer player, int slot, ItemStack stack, boolean simulate);

	ItemStack extractItem(EntityPlayer player, int slot, int amount, boolean simulate);

	int getSlotLimit(EntityPlayer player, int slot);

//	int getStackLimit(EntityPlayer player, int slot, ItemStack stack);

	ItemStackHandler getUpgradeHandler();

	void setUpgradeStackInSlot(int slot, @Nonnull ItemStack stack);

	int getUpgradeSlots();

	@Nonnull
	ItemStack getUpgradeStackInSlot(int slot);

	@Nonnull
	ItemStack insertUpgradeItem(int slot, @Nonnull ItemStack stack, boolean simulate);

	@Nonnull
	ItemStack extractUpgradeItem(int slot, int amount, boolean simulate);

	int getUpgradeSlotLimit(int slot);

	void setEnderUpgrade(ItemStack enderUpgrade);

//	ItemStack getEnderUpgrade();

}
