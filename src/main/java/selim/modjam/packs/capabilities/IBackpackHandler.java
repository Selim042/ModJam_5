package selim.modjam.packs.capabilities;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public interface IBackpackHandler extends IItemHandlerModifiable {

	void setUpgradeStackInSlot(int slot, @Nonnull ItemStack stack);

	int getUpgradeSlots();

	@Nonnull
	ItemStack getUpgradeStackInSlot(int slot);

	@Nonnull
	ItemStack insertUpgradeItem(int slot, @Nonnull ItemStack stack, boolean simulate);

	@Nonnull
	ItemStack extractUpgradeItem(int slot, int amount, boolean simulate);

	int getUpgradeSlotLimit(int slot);

}
