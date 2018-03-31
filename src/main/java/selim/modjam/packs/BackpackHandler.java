package selim.modjam.packs;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.ItemStackHandler;

public class BackpackHandler extends ItemStackHandler implements IBackpackHandler {

	private static final int PACK_SIZE = 18;

	protected BackpackHandler() {
		super(PACK_SIZE);
	}

	public BackpackHandler(ItemStack chestplate) {
		super(PACK_SIZE);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability.equals(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability.equals(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY))
			return CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY.cast(this);
		return null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return super.serializeNBT();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
	}

}
