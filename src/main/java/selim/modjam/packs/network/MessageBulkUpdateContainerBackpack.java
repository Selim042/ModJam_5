package selim.modjam.packs.network;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidTank;
import selim.modjam.packs.BackpackHandler;
import selim.modjam.packs.CapabilityBackpackHandler;
import selim.modjam.packs.IBackpackHandler;

public class MessageBulkUpdateContainerBackpack
		extends MessageBulkUpdateContainerCapability<IBackpackHandler, NBTTagCompound> {

	public MessageBulkUpdateContainerBackpack() {
		super(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY);
	}

	public MessageBulkUpdateContainerBackpack(final int windowID, final NonNullList<ItemStack> items) {
		super(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null, windowID, items);
	}

	@Nullable
	@Override
	protected NBTTagCompound convertCapabilityToData(final IBackpackHandler fluidHandlerItem) {
		if (fluidHandlerItem instanceof BackpackHandler)
			return ((BackpackHandler) fluidHandlerItem).serializeNBT();
		else
			return null;
	}

	@Override
	protected NBTTagCompound readCapabilityData(final ByteBuf buf) {
//		System.out.println("reading bulk");
		return MessageUpdateContainerBackpack.readNBTTagCompound(buf);
	}

	@Override
	protected void writeCapabilityData(final ByteBuf buf, final NBTTagCompound NBTTagCompound) {
		MessageUpdateContainerBackpack.writeNBTTagCompound(buf, NBTTagCompound);
	}

	public static class Handler extends
			MessageBulkUpdateContainerCapability.Handler<IBackpackHandler, NBTTagCompound, MessageBulkUpdateContainerBackpack> {

		@Override
		protected void applyCapabilityData(IBackpackHandler fluidHandlerItem,
				NBTTagCompound nbt) {
			if (fluidHandlerItem instanceof BackpackHandler)
				((BackpackHandler) fluidHandlerItem).deserializeNBT(nbt);
		}
	}
}