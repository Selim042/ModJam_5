package selim.modjam.packs;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import selim.modjam.packs.capabilities.CapabilityBackpackHandler;

public class BackpackTab extends CreativeTabs {

	private static final List<ItemStack> BACKPACKS = new LinkedList<ItemStack>();

	public BackpackTab() {
		super(ModJamPacks.MODID);
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(PacksItems.BACKPACK);
	}

	public static void addBackpack(ItemStack stack) {
		if (!BACKPACKS.contains(stack) && stack.getItem() instanceof ItemArmor
				&& ((ItemArmor) stack.getItem()).armorType == EntityEquipmentSlot.CHEST
				&& stack.hasCapability(CapabilityBackpackHandler.BACKPACK_HANDLER_CAPABILITY, null))
			BACKPACKS.add(stack);
	}

	@Override
	public void displayAllRelevantItems(NonNullList<ItemStack> items) {
		super.displayAllRelevantItems(items);
		items.addAll(BACKPACKS);
	}

}
