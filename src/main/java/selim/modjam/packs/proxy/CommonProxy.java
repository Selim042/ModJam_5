package selim.modjam.packs.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy {

	public IThreadListener getThreadListener(final MessageContext context) {
		if (context.side.isServer())
			return context.getServerHandler().player.mcServer;
		else
			throw new IllegalArgumentException(
					"Tried to get the IThreadListener from a client-side MessageContext on the dedicated server");
	}

	public EntityPlayer getPlayer(final MessageContext context) {
		if (context.side.isServer()) {
			return context.getServerHandler().player;
		} else {
			throw new IllegalArgumentException(
					"Tried to get the player from a client-side MessageContext on the dedicated server");
		}
	}

}
