package mekanism.client.gui.filter;

import java.io.IOException;
import java.util.List;
import mekanism.api.Coord4D;
import mekanism.client.gui.button.GuiButtonDisableableImage;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.Mekanism;
import mekanism.common.inventory.container.ContainerFilter;
import mekanism.common.network.PacketEditFilter;
import mekanism.common.network.PacketNewFilter;
import mekanism.common.network.PacketSimpleGui;
import mekanism.common.tile.TileEntityOredictionificator;
import mekanism.common.tile.TileEntityOredictionificator.OredictionificatorFilter;
import mekanism.common.util.ItemRegistryUtils;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.oredict.OreDictionary;

@OnlyIn(Dist.CLIENT)
public class GuiOredictionificatorFilter extends GuiTextFilterBase<OredictionificatorFilter, TileEntityOredictionificator> {

    private Button prevButton;
    private Button nextButton;
    private Button checkboxButton;

    public GuiOredictionificatorFilter(PlayerEntity player, TileEntityOredictionificator tile, int index) {
        super(tile, new ContainerFilter(player.inventory, tile));
        origFilter = tileEntity.filters.get(index);
        filter = tileEntity.filters.get(index).clone();
        updateRenderStack();
    }

    public GuiOredictionificatorFilter(PlayerEntity player, TileEntityOredictionificator tile) {
        super(tile, new ContainerFilter(player.inventory, tile));
        filter = new OredictionificatorFilter();
        isNew = true;
    }

    @Override
    protected void addButtons() {
        buttonList.add(saveButton = new Button(0, guiLeft + 31, guiTop + 62, 54, 20, LangUtils.localize("gui.save")));
        buttonList.add(deleteButton = new Button(1, guiLeft + 89, guiTop + 62, 54, 20, LangUtils.localize("gui.delete")));
        buttonList.add(backButton = new GuiButtonDisableableImage(2, guiLeft + 5, guiTop + 5, 11, 11, 212, 11, -11, getGuiLocation()));
        buttonList.add(prevButton = new GuiButtonDisableableImage(3, guiLeft + 31, guiTop + 21, 12, 12, 200, 12, -12, getGuiLocation()));
        buttonList.add(nextButton = new GuiButtonDisableableImage(4, guiLeft + 63, guiTop + 21, 12, 12, 188, 12, -12, getGuiLocation()));
        buttonList.add(checkboxButton = new GuiButtonDisableableImage(5, guiLeft + 130, guiTop + 48, 12, 12, 176, 12, -12, getGuiLocation()));
    }

    @Override
    protected void sendPacketToServer(int guiID) {
        Mekanism.packetHandler.sendToServer(new PacketSimpleGui(Coord4D.get(tileEntity), 0, guiID));
    }

    @Override
    public void setText() {
        String newFilter = text.getText();
        if (TileEntityOredictionificator.possibleFilters.stream().anyMatch(newFilter::startsWith)) {
            filter.filter = newFilter;
            filter.index = 0;
            text.setText("");
            updateRenderStack();
        }
        updateButtons();
    }

    public void updateButtons() {
        saveButton.enabled = filter.filter != null && !filter.filter.isEmpty();
        deleteButton.enabled = !isNew;
    }

    @Override
    protected TextFieldWidget createTextField() {
        return new TextFieldWidget(2, fontRenderer, guiLeft + 33, guiTop + 48, 96, 12);
    }

    @Override
    public void initGui() {
        super.initGui();
        updateButtons();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String text = (isNew ? LangUtils.localize("gui.new") : LangUtils.localize("gui.edit")) + " " + LangUtils.localize("gui.filter");
        fontRenderer.drawString(text, (xSize / 2) - (fontRenderer.getStringWidth(text) / 2), 6, 0x404040);
        fontRenderer.drawString(LangUtils.localize("gui.index") + ": " + filter.index, 79, 23, 0x404040);
        if (filter.filter != null) {
            renderScaledText(filter.filter, 32, 38, 0x404040, 111);
        }
        renderItem(renderStack, 45, 19);
        int xAxis = mouseX - guiLeft;
        int yAxis = mouseY - guiTop;
        if (xAxis >= 31 && xAxis <= 43 && yAxis >= 21 && yAxis <= 33) {
            displayTooltip(LangUtils.localize("gui.lastItem"), xAxis, yAxis);
        } else if (xAxis >= 63 && xAxis <= 75 && yAxis >= 21 && yAxis <= 33) {
            displayTooltip(LangUtils.localize("gui.nextItem"), xAxis, yAxis);
        } else if (xAxis >= 33 && xAxis <= 129 && yAxis >= 48 && yAxis <= 60) {
            displayTooltip(LangUtils.localize("gui.oreDictCompat"), xAxis, yAxis);
        } else if (xAxis >= 45 && xAxis <= 61 && yAxis >= 19 && yAxis <= 35) {
            if (!renderStack.isEmpty()) {
                String name = ItemRegistryUtils.getMod(renderStack);
                String extra = name.equals("null") ? "" : " (" + name + ")";
                displayTooltip(renderStack.getDisplayName() + extra, xAxis, yAxis);
            }
        }
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int xAxis, int yAxis) {
        super.drawGuiContainerBackgroundLayer(xAxis, yAxis);
        text.drawTextBox();
        MekanismRenderer.resetColor();
    }

    @Override
    protected void actionPerformed(Button guibutton) throws IOException {
        super.actionPerformed(guibutton);
        if (guibutton.id == saveButton.id) {
            if (!text.getText().isEmpty()) {
                setText();
            }
            if (filter.filter != null && !filter.filter.isEmpty()) {
                if (isNew) {
                    Mekanism.packetHandler.sendToServer(new PacketNewFilter(Coord4D.get(tileEntity), filter));
                } else {
                    Mekanism.packetHandler.sendToServer(new PacketEditFilter(Coord4D.get(tileEntity), false, origFilter, filter));
                }
                sendPacketToServer(52);
            }
        } else if (guibutton.id == deleteButton.id) {
            Mekanism.packetHandler.sendToServer(new PacketEditFilter(Coord4D.get(tileEntity), true, origFilter, null));
            sendPacketToServer(52);
        } else if (guibutton.id == backButton.id) {
            sendPacketToServer(52);
        } else if (guibutton.id == prevButton.id) {
            if (filter.filter != null) {
                List<ItemStack> ores = OreDictionary.getOres(filter.filter, false);
                if (filter.index > 0) {
                    filter.index--;
                } else {
                    filter.index = ores.size() - 1;
                }
                updateRenderStack();
            }
        } else if (guibutton.id == nextButton.id) {
            if (filter.filter != null) {
                List<ItemStack> ores = OreDictionary.getOres(filter.filter, false);
                if (filter.index < ores.size() - 1) {
                    filter.index++;
                } else {
                    filter.index = 0;
                }
                updateRenderStack();
            }
        } else if (guibutton.id == checkboxButton.id) {
            setText();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        text.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "GuiOredictionificatorFilter.png");
    }

    private void updateRenderStack() {
        if (filter.filter == null || filter.filter.isEmpty()) {
            renderStack = ItemStack.EMPTY;
            return;
        }
        List<ItemStack> stacks = OreDictionary.getOres(filter.filter, false);
        if (stacks.isEmpty()) {
            renderStack = ItemStack.EMPTY;
            return;
        }
        if (stacks.size() - 1 >= filter.index) {
            renderStack = stacks.get(filter.index).copy();
        } else {
            renderStack = ItemStack.EMPTY;
        }
    }
}