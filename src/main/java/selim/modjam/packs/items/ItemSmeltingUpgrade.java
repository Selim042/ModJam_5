package selim.modjam.packs.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import selim.modjam.packs.ModJamPacks;
import selim.modjam.packs.capabilities.IBackpackHandler;

public class ItemSmeltingUpgrade extends Item implements IBackpackUpgrade {

	public ItemSmeltingUpgrade() {
		this.setRegistryName(new ResourceLocation(ModJamPacks.MODID, "smelting_upgrade"));
		this.setUnlocalizedName(ModJamPacks.MODID + ":smelting_upgrade");
		this.setMaxStackSize(1);
		this.setCreativeTab(ModJamPacks.CREATIVE_TAB);
	}

	@Override
	public ItemStack onItemAdd(IBackpackHandler backpack, ItemStack added) {
		ItemStack result = FurnaceRecipes.instance().getSmeltingResult(added).copy();
		result.setCount(result.getCount() * added.getCount());
		if (result.isEmpty())
			return added;
		return result;
	}

}
