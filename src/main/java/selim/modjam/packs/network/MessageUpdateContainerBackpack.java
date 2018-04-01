package selim.modjam.packs.network;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import selim.modjam.packs.BackpackHandler;
import selim.modjam.packs.CapabilityBackpackHandler;
import selim.modjam.packs.IBackpackHandler;

public class MessageUpdateContainerBackpack
		extends MessageUpdateContainerCapability<IBackpackHandler, NBTTagCompound> {

	public MessageUpdateContainerBackpack() {
		super(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY);
	}

	public MessageUpdateContainerBackpack(final int windowID, final int slotNumber,
			final IBackpackHandler fluidHandlerItem) {
		super(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null, windowID, slotNumber,
				fluidHandlerItem);
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
//		System.out.println("reading single");
		return readNBTTagCompound(buf);
	}

	@Override
	protected void writeCapabilityData(final ByteBuf buf, final NBTTagCompound fluidTankInfo) {
		writeNBTTagCompound(buf, fluidTankInfo);
	}

	static NBTTagCompound readNBTTagCompound(final ByteBuf buf) {
		return ByteBufUtils.readTag(buf);
	}

	static void writeNBTTagCompound(final ByteBuf buf, final NBTTagCompound fluidTankInfo) {
		ByteBufUtils.writeTag(buf, fluidTankInfo);
	}

	public static class Handler extends
			MessageUpdateContainerCapability.Handler<IBackpackHandler, NBTTagCompound, MessageUpdateContainerBackpack> {

		@Override
		protected void applyCapabilityData(final IBackpackHandler fluidHandlerItem,
				final NBTTagCompound fluidTankInfo) {
			if (fluidHandlerItem instanceof BackpackHandler)
				((BackpackHandler) fluidHandlerItem).deserializeNBT(fluidTankInfo);
		}
	}

}