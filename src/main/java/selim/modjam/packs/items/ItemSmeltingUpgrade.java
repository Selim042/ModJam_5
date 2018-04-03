package selim.modjam.packs.items;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import selim.modjam.packs.ModJamPacks;
import selim.modjam.packs.capabilities.IBackpackHandler;

public class ItemSmeltingUpgrade extends Item implements IBackpackUpgrade {

	public ItemSmeltingUpgrade() {
		this.setRegistryName(new ResourceLocation(ModJamPacks.MODID, "smelting_upgrade"));
		this.setUnlocalizedName(ModJamPacks.MODID + ":smelting_upgrade");
		this.setMaxStackSize(1);
		this.setCreativeTab(ModJamPacks.CREATIVE_TAB);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip,
			ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		// TODO: Fix this
		tooltip.add("Currently only works in the first upgrade slot.");
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
