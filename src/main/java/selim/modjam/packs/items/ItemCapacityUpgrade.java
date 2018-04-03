package selim.modjam.packs.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import selim.modjam.packs.ModJamPacks;

public class ItemCapacityUpgrade extends Item implements IBackpackUpgrade {

	public ItemCapacityUpgrade() {
		this.setRegistryName(new ResourceLocation(ModJamPacks.MODID, "capacity_upgrade"));
		this.setUnlocalizedName(ModJamPacks.MODID + ":capacity_upgrade");
		this.setMaxStackSize(1);
		this.setCreativeTab(ModJamPacks.CREATIVE_TAB);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new CapacityUpgradeProvider();
	}

	private static class CapacityUpgradeProvider extends ItemStackHandler
			implements ICapabilityProvider {

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this);
			return null;
		}

	}

}