package selim.modjam.packs;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.items.IItemHandlerModifiable;
import selim.modjam.packs.network.CapabilityContainerListenerManager;
import selim.modjam.packs.network.ContainerListenerBackpack;

public class CapabilityBackpackHandler {

	private static boolean registered = false;

	@CapabilityInject(IBackpackHandler.class)
	public static Capability<IBackpackHandler> BACKPACK_HANDLER_CAPABILITY = null;

	public static void register() {
		if (registered)
			return;
		CapabilityManager.INSTANCE.register(IBackpackHandler.class,
				new Capability.IStorage<IBackpackHandler>() {

					@Override
					public NBTBase writeNBT(Capability<IBackpackHandler> capability,
							IBackpackHandler instance, EnumFacing side) {
						NBTTagList nbtTagList = new NBTTagList();
						int size = instance.getSlots();
						for (int i = 0; i < size; i++) {
							ItemStack stack = instance.getStackInSlot(i);
							if (!stack.isEmpty()) {
								NBTTagCompound itemTag = new NBTTagCompound();
								itemTag.setInteger("Slot", i);
								stack.writeToNBT(itemTag);
								nbtTagList.appendTag(itemTag);
							}
						}
						return nbtTagList;
					}

					@Override
					public void readNBT(Capability<IBackpackHandler> capability,
							IBackpackHandler instance, EnumFacing side, NBTBase base) {
						if (!(instance instanceof IItemHandlerModifiable))
							throw new RuntimeException(
									"IItemHandler instance does not implement IItemHandlerModifiable");
						IItemHandlerModifiable itemHandlerModifiable = (IItemHandlerModifiable) instance;
						NBTTagList tagList = (NBTTagList) base;
						for (int i = 0; i < tagList.tagCount(); i++) {
							NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
							int j = itemTags.getInteger("Slot");

							if (j >= 0 && j < instance.getSlots()) {
								itemHandlerModifiable.setStackInSlot(j, new ItemStack(itemTags));
							}
						}
					}
				}, BackpackHandler::new);
		CapabilityContainerListenerManager.registerListenerFactory(ContainerListenerBackpack::new);
	}

}
