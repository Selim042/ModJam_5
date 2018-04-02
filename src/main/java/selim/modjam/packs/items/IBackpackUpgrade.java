package selim.modjam.packs.items;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import selim.modjam.packs.capabilities.IBackpackHandler;

public interface IBackpackUpgrade {

	/**
	 * Called when any item is added to the backpack. Use to modify what is
	 * added. Return null to cancel addition of item.
	 * 
	 * @param backpack
	 *            The ItemStack being used as a backpack
	 * @param added
	 *            The ItemStack to be added to the backpack
	 * @return The ItemStack to store in the backpack
	 */
	@Nullable
	public default ItemStack onItemAdd(IBackpackHandler backpack, ItemStack added) {
		return added;
	}

}
