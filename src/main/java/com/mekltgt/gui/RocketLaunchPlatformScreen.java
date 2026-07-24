package com.mekltgt.gui;

import com.mekltgt.blockentity.RocketLaunchPlatformBlockEntity;
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

public class RocketLaunchPlatformScreen extends GuiMekanismTile<RocketLaunchPlatformBlockEntity, MekanismTileContainer<RocketLaunchPlatformBlockEntity>> {

    public RocketLaunchPlatformScreen(MekanismTileContainer<RocketLaunchPlatformBlockEntity> container, Inventory inv, Component title) {
        super(container, inv, title);
        dynamicSlots = true;
    }

    @Override
    protected void addGuiElements() {
        addRenderableWidget(GuiSideHolder.create(this, -26, 6, 98, true, true,
                SpecialColors.TAB_ARMOR_SLOTS));

        super.addGuiElements();

        addRenderableWidget(new GuiInnerScreen(this, 48, 21, 80, 44, () -> {
            List<Component> list = new ArrayList<>();
            list.add(Component.literal("")
                    .append(EnergyDisplay.of(tile.getEnergyStoredLong()).getTextComponent())
                    .append(Component.literal(" / "))
                    .append(EnergyDisplay.of(tile.getEnergyContainer().getMaxEnergy()).getTextComponent()));

            if (!tile.hasSkyAccess()) {
                list.add(Component.translatable("gui.mekltgt.rocket_launch_platform.obstructed")
                        .withStyle(ChatFormatting.DARK_RED));
            } else if (tile.isCoolingDown()) {
                int seconds = tile.getCooldownTicks() / 20;
                list.add(Component.translatable("gui.mekltgt.rocket_launch_platform.cooldown", seconds)
                        .withStyle(ChatFormatting.GOLD));
            } else if (tile.isActive()) {
                int seconds = tile.getActiveTicks() / 20;
                list.add(Component.translatable("gui.mekltgt.rocket_launch_platform.active", seconds)
                        .withStyle(ChatFormatting.GREEN));
            } else if (tile.getLevel() != null && tile.getLevel().isThundering()) {
                list.add(Component.translatable("gui.mekltgt.rocket_launch_platform.ready_storm")
                        .withStyle(ChatFormatting.AQUA));
            } else {
                list.add(Component.translatable("gui.mekltgt.rocket_launch_platform.ready")
                        .withStyle(ChatFormatting.AQUA));
            }
            return list;
        }));

        addRenderableWidget(new GuiEnergyTab(this, () -> {
            List<Component> list = new ArrayList<>();
            list.add(MekanismLang.STORED_ENERGY.translate(EnergyDisplay.of(tile.getEnergyStoredLong())));
            return list;
        }));

        addRenderableWidget(new GuiVerticalPowerBar(this, tile.getEnergyContainer(), 164, 15));
    }

    @Override
    protected void drawForegroundText(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderTitleText(guiGraphics);
        renderInventoryText(guiGraphics);
        super.drawForegroundText(guiGraphics, mouseX, mouseY);
    }
}