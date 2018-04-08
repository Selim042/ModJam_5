package selim.modjam.packs.network;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import selim.modjam.packs.ModJamPacks;
import selim.modjam.packs.capabilities.CapabilityBackpackHandler;
import selim.modjam.packs.wrappers.BackpackHandlerWrapper;

public class MessageOpenBackpack implements IMessage {

	@Override
	public final void fromBytes(final ByteBuf buf) {}

	@Override
	public final void toBytes(final ByteBuf buf) {}

	public static class Handler implements IMessageHandler<MessageOpenBackpack, IMessage> {

		@Nullable
		@Override
		public final IMessage onMessage(final MessageOpenBackpack message, final MessageContext ctx) {
			if (ctx.side.isClient())
				return null;
			EntityPlayer player = ModJamPacks.proxy.getPlayer(ctx);
			ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			if (stack.isEmpty()
					|| !stack.hasCapability(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null)
					|| !(player instanceof EntityPlayerMP))
				return null;
			EntityPlayerMP playerMP = (EntityPlayerMP) player;

//			ModJamPacks.network.sendTo(
//					new MessageUpdateContainerBackpack(playerMP.inventoryContainer.windowId,
//							EntityEquipmentSlot.CHEST.getSlotIndex(), stack.getCapability(
//									CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null)),
//					playerMP);
			playerMP.displayGUIChest(new BackpackHandlerWrapper(stack, playerMP));
			// playerMP.openGui(ModJamPacks.instance, 0, null, 0, 0, 0);
			return null;
		}

	}

}