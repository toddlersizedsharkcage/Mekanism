package mekanism.client.gui;

import mekanism.client.gui.element.GuiEnergyInfo;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.tile.TileEntitySeismicVibrator;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GuiSeismicVibrator extends GuiMekanismTile<TileEntitySeismicVibrator, MekanismTileContainer<TileEntitySeismicVibrator>> {

    public GuiSeismicVibrator(MekanismTileContainer<TileEntitySeismicVibrator> container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        dynamicSlots = true;
    }

    @Override
    public void init() {
        super.init();
        addButton(new GuiInnerScreen(this, 16, 23, 112, 40));
        addButton(new GuiSecurityTab<>(this, tile));
        addButton(new GuiRedstoneControl(this, tile));
        addButton(new GuiVerticalPowerBar(this, tile.getEnergyContainer(), 164, 15));
        addButton(new GuiEnergyInfo(tile.getEnergyContainer(), this));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawString(tile.getName(), 45, 6, titleTextColor());
        drawString(MekanismLang.INVENTORY.translate(), 8, (getYSize() - 96) + 2, titleTextColor());
        drawString(tile.getActive() ? MekanismLang.VIBRATING.translate() : MekanismLang.IDLE.translate(), 19, 26, screenTextColor());
        drawString(MekanismLang.CHUNK.translate(tile.getPos().getX() >> 4, tile.getPos().getZ() >> 4), 19, 35, screenTextColor());
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
}