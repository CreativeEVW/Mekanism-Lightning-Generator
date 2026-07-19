package com.mekltgt.gui;

import com.mekltgt.blockentity.LightningGeneratorBlockEntity;
import mekanism.client.SpecialColors;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.GuiSideHolder;
import mekanism.client.gui.element.bar.GuiVerticalPowerBar;
import mekanism.client.gui.element.tab.GuiEnergyTab;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.util.text.EnergyDisplay;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LightningGeneratorScreen extends GuiMekanismTile<LightningGeneratorBlockEntity, MekanismTileContainer<LightningGeneratorBlockEntity>> {

    public LightningGeneratorScreen(MekanismTileContainer<LightningGeneratorBlockEntity> container, Inventory inv, Component title) {
        super(container, inv, title);
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        // 盔甲槽侧边背景（与风力发电机完全相同的参数）
        addRenderableWidget(GuiSideHolder.create(this, -26, 6, 98, true, true,
                SpecialColors.TAB_ARMOR_SLOTS));

        super.addGuiElements();

        // 中部信息面板（仿风力发电机）
        addRenderableWidget(new GuiInnerScreen(this, 48, 21, 80, 44, () -> {
            List<Component> list = new ArrayList<>();
            // 第一行：当前FE / 最大FE
            list.add(Component.literal("")
                    .append(EnergyDisplay.of(tile.getEnergyStoredLong()).getTextComponent())
                    .append(Component.literal(" / "))
                    .append(EnergyDisplay.of(tile.getCurrentMaxEnergy()).getTextComponent()));
            // 第二行：输出 rate
            list.add(Component.translatable("gui.mekltgt.lightning_generator.avg_production",
                    EnergyDisplay.of(tile.getAverageProduction()).getTextComponent()));
            // 第三行：工作状态（前置条件不满足时显示深红色）
            if (!tile.isProbeValid()) {
                list.add(Component.translatable("gui.mekltgt.lightning_generator.probe_missing")
                        .withStyle(ChatFormatting.DARK_RED));
            }
            return list;
        }));

        // 能量信息标签页
        addRenderableWidget(new GuiEnergyTab(this, () -> {
            long stored = tile.getEnergyStoredLong();
            long avgProd = tile.getAverageProduction();
            List<Component> list = new ArrayList<>();
            list.add(MekanismLang.STORED_ENERGY.translate(EnergyDisplay.of(stored)));
            list.add(Component.translatable("gui.mekltgt.lightning_generator.avg_production",
                    EnergyDisplay.of(avgProd).getTextComponent()));
            return list;
        }));

        // 垂直能量条
        addRenderableWidget(new GuiVerticalPowerBar(this, tile.getEnergyContainer(), 164, 15));
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        renderInventoryText(guiGraphics);
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }
}
