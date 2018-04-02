package selim.modjam.packs.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import selim.modjam.packs.ModJamPacks;
import selim.modjam.packs.capabilities.BackpackHandler;

public class ItemBackpack extends ItemArmor {

	public ItemBackpack() {
		super(ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.CHEST);
		this.setRegistryName(new ResourceLocation(ModJamPacks.MODID, "backpack"));
		this.setUnlocalizedName(ModJamPacks.MODID + ":backpack");
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.COMBAT);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new BackpackHandler(stack);
	}

}
