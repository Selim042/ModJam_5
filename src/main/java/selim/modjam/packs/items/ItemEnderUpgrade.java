package selim.modjam.packs.items;

import java.util.List;

import codechicken.lib.colour.EnumColour;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import selim.modjam.packs.ModJamPacks;
import selim.modjam.packs.capabilities.IBackpackHandler;
import selim.modjam.packs.compat.EnderStorageHelper;

public class ItemEnderUpgrade extends Item implements IBackpackUpgrade {

	public ItemEnderUpgrade() {
		this.setRegistryName(new ResourceLocation(ModJamPacks.MODID, "ender_upgrade"));
		this.setUnlocalizedName(ModJamPacks.MODID + ":ender_upgrade");
		this.setMaxStackSize(1);
		this.setCreativeTab(ModJamPacks.CREATIVE_TAB);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip,
			ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		if (Loader.isModLoaded(EnderStorageHelper.ID) && stack.getSubCompound("freq") != null) {
			NBTTagCompound freq = stack.getSubCompound("freq");
			// TODO: Add color bound to (& owner)
		}
	}

	public IItemHandlerModifiable getEnderInventory(EntityPlayer player, ItemStack stack) {
		if (Loader.isModLoaded(EnderStorageHelper.ID) && stack.getSubCompound("freq") != null)
			return EnderStorageHelper.getInventory(stack.getSubCompound("freq"));
		return new InvWrapper(player.getInventoryEnderChest());
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!Loader.isModLoaded(EnderStorageHelper.ID))
			return EnumActionResult.PASS;
		if (world.isRemote)
			return EnumActionResult.PASS;
		ItemStack held = player.getHeldItem(hand);
		if (EnderStorageHelper.isEnderChest(world, pos)) {
			NBTTagCompound freqNbt = EnderStorageHelper.getFrequency(world, pos);
			NBTTagCompound nbt = held.getTagCompound();
			if (nbt == null)
				nbt = new NBTTagCompound();
			nbt.setTag("freq", freqNbt);
			held.setTagCompound(nbt);
			return EnumActionResult.SUCCESS;
		} else {
			NBTTagCompound nbt = held.getTagCompound();
			if (nbt == null)
				return EnumActionResult.SUCCESS;
			nbt.removeTag("freq");
			held.setTagCompound(nbt);
			return EnumActionResult.SUCCESS;
		}
	}

	@Override
	public void onUpgradeAdded(IBackpackHandler backpack, ItemStack added) {
		backpack.setEnderUpgrade(added);
	}

	@Override
	public void onUpgradeRemoved(IBackpackHandler backpack, ItemStack added) {
		backpack.setEnderUpgrade(ItemStack.EMPTY);
	}

	public static class EnderUpgradeItemColor implements IItemColor {

		@Override
		public int colorMultiplier(ItemStack stack, int tintIndex) {
			NBTTagCompound nbt = stack.getSubCompound("freq");
			if (!Loader.isModLoaded(EnderStorageHelper.ID) || nbt == null)
				return -1;
			switch (tintIndex) {
			case 0: // base texture
			default:
				return -1;
			case 1: // left
				return EnumColour.fromDyeMeta(nbt.getInteger("left")).rgb();
			case 2: // middle
				return EnumColour.fromDyeMeta(nbt.getInteger("middle")).rgb();
			case 3: // right
				return EnumColour.fromDyeMeta(nbt.getInteger("right")).rgb();
			}
		}

	}

	public static class EnderMeshDefinition implements ItemMeshDefinition {

		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			if (Loader.isModLoaded(EnderStorageHelper.ID) && stack.getSubCompound("freq") != null) {
				String owner = stack.getSubCompound("freq").getString("owner");
				if (owner == null || owner.equals(""))
					return new ModelResourceLocation(
							new ResourceLocation(ModJamPacks.MODID, "ender_upgrade_unowned"),
							"inventory");
				else
					return new ModelResourceLocation(
							new ResourceLocation(ModJamPacks.MODID, "ender_upgrade_owned"), "inventory");
			}
			return new ModelResourceLocation(new ResourceLocation(ModJamPacks.MODID, "ender_upgrade"),
					"inventory");
		}

	}

}