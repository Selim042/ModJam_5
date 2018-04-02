package selim.modjam.packs.items;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import selim.modjam.packs.ModJamPacks;
import selim.modjam.packs.capabilities.CapabilityBackpackHandler;
import selim.modjam.packs.capabilities.IBackpackHandler;

@Mod.EventBusSubscriber
public class ItemCollectionUpgrade extends Item implements IBackpackUpgrade {

	public ItemCollectionUpgrade() {
		this.setRegistryName(new ResourceLocation(ModJamPacks.MODID, "collection_upgrade"));
		this.setUnlocalizedName(ModJamPacks.MODID + ":collection_upgrade");
		this.setMaxStackSize(1);
		this.setCreativeTab(ModJamPacks.CREATIVE_TAB);
	}

	@SubscribeEvent
	public static void onPickup(EntityItemPickupEvent event) {
		ItemStack pickupStack = event.getItem().getItem().copy();
		EntityPlayer player = event.getEntityPlayer();
		if (player == null)
			return;
		ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if (stack.isEmpty()
				|| !stack.hasCapability(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null))
			return;
		IBackpackHandler backpack = stack
				.getCapability(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null);
		for (int s = 0; s < backpack.getUpgradeSlots(); s++) {
			if (backpack.getUpgradeStackInSlot(s).getItem() instanceof ItemCollectionUpgrade) {
				for (int i = 0; !stack.isEmpty() && i < backpack.getSlots(); i++)
					stack = backpack.insertItem(i, stack, false);
				if (!stack.isEmpty()) {
					if (areStacksSimilar(pickupStack, stack, 0)) 
						return;
					event.setResult(Result.ALLOW);
					World world = player.world;
					EntityItem oldItem = event.getItem();
					world.spawnEntity(
							new EntityItem(world, oldItem.posX, oldItem.posY, oldItem.posZ, stack));
					event.getItem().setDead();
					// player.sendMessage(new TextComponentString("test"));
					// event.getItem().getItem().setCount(stack.getCount());
				} else {
					event.setResult(Result.ALLOW);
					event.getItem().setDead();
				}
			}
		}
	}

	/**
	 * Flags: 0, none, 1 ignore metadata, 2 ignore count, 4 ignore NBT
	 */
	private static boolean areStacksSimilar(ItemStack a, ItemStack b, int flags) {
		return a.getItem().equals(b.getItem()) && ((flags & 1) > 0 || a.getMetadata() == b.getMetadata())
				&& ((flags & 2) > 0 || a.getCount() == b.getCount())
				&& ((flags & 4) > 0 || (a.hasTagCompound() == b.hasTagCompound() && a.hasTagCompound()
						&& a.getTagCompound().equals(b.getTagCompound())));
	}

}
