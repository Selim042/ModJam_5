package selim.modjam.packs.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy extends CommonProxy {

	@Override
	public IThreadListener getThreadListener(final MessageContext context) {
		if (context.side.isClient())
			return Minecraft.getMinecraft();
		else
			return context.getServerHandler().player.mcServer;
	}

	@Override
	public EntityPlayer getPlayer(final MessageContext context) {
		if (context.side.isClient())
			return Minecraft.getMinecraft().player;
		else
			return context.getServerHandler().player;
	}

}
