package selim.modjam.packs.compat;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.init.ModBlocks;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.enderstorage.tile.TileEnderChest;
import codechicken.lib.colour.EnumColour;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

public class EnderStorageHelper {

	public static final String ID = "enderstorage";

	public static boolean isEnderChest(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return state != null && state.getBlock().equals(ModBlocks.blockEnderStorage);
	}

	public static NBTTagCompound getFrequency(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te == null || !(te instanceof TileEnderChest))
			return null;
		TileEnderChest chest = (TileEnderChest) te;
		NBTTagCompound freq = new NBTTagCompound();
		freq.setInteger("left", chest.frequency.getLeft().getDyeMeta());
		freq.setInteger("middle", chest.frequency.getMiddle().getDyeMeta());
		freq.setInteger("right", chest.frequency.getRight().getDyeMeta());
		if (chest.frequency.hasOwner())
			freq.setString("owner", chest.frequency.owner);
		return freq;
	}

	public static IItemHandlerModifiable getInventory(NBTTagCompound freqNbt) {
		if (!freqNbt.hasKey("left") || !freqNbt.hasKey("middle") || !freqNbt.hasKey("right"))
			return null;
		Frequency freq;
		if (freqNbt.hasKey("owner"))
			freq = new Frequency(EnumColour.fromDyeMeta(freqNbt.getInteger("left")),
					EnumColour.fromDyeMeta(freqNbt.getInteger("middle")),
					EnumColour.fromDyeMeta(freqNbt.getInteger("right")), freqNbt.getString("owner"));
		else
			freq = new Frequency(EnumColour.fromDyeMeta(freqNbt.getInteger("left")),
					EnumColour.fromDyeMeta(freqNbt.getInteger("middle")),
					EnumColour.fromDyeMeta(freqNbt.getInteger("right")));
		return new InvWrapper(
				(EnderItemStorage) EnderStorageManager.instance(false).getStorage(freq, "item"));
	}

}
