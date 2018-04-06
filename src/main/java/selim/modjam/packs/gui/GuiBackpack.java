package selim.modjam.packs.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBackpack extends GuiContainer {

	private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation(
			"textures/gui/container/generic_54.png");

	private final ContainerBackpack container;
	private final EntityPlayer player;
	private final int numRows;

	public GuiBackpack(ContainerBackpack container, EntityPlayer player) {
		super(container);
		this.container = container;
		this.player = player;
		this.numRows = container.getBackpack().getSlots() / 9;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		for (int row = 0; row < this.numRows; row++)
			this.drawTexturedModalRect(i, j, 0, 0, this.xSize, row * 18 + 17);
		this.drawTexturedModalRect(i, j + this.numRows * 18 + 17, 0, 126, this.xSize, 96);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRenderer.drawString(this.player.inventory.getDisplayName().getUnformattedText(), 8, 6,
				4210752);
		// TODO: Fix below to how it used to be
		this.fontRenderer.drawString("Backpack", 8, this.ySize - 96 + 2, 4210752);
	}

}
