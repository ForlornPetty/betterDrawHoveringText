package com.petty.betterdrawhoveringtext;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.fml.client.config.GuiUtils.drawGradientRect;

@Mod(modid = "bdht", name = "betterDrawHoveringText", version = "1.0.0")
public class betterDrawHoveringText {
    static double subtractY;static double inertia;static ItemStack itemStack;
    @Mod.EventHandler
    void preInit(FMLPreInitializationEvent event) {MinecraftForge.EVENT_BUS.register(this);}
    @SubscribeEvent(priority = EventPriority.LOWEST)
    void RenderTooltipEvent(RenderTooltipEvent.Pre renderTooltipEvent) {drawHoveringText(renderTooltipEvent.getStack(), renderTooltipEvent.getLines(), renderTooltipEvent.getX(), renderTooltipEvent.getY(), renderTooltipEvent.getScreenWidth(), renderTooltipEvent.getScreenHeight(), renderTooltipEvent.getMaxWidth(), renderTooltipEvent.getFontRenderer());renderTooltipEvent.setCanceled(true);}
    void drawHoveringText(@Nonnull final ItemStack stack, List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font) {
        if (!textLines.isEmpty()) {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int tooltipTextWidth = 0;
            for (String textLine : textLines) {
                int textLineWidth = font.getStringWidth(textLine);
                if (textLineWidth > tooltipTextWidth) {
                    tooltipTextWidth = textLineWidth;
                }
            }
            boolean needsWrap = false;
            int titleLinesCount = 1;
            int tooltipX = mouseX + 12;
            if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
                tooltipX = mouseX - 16 - tooltipTextWidth;
                if (tooltipX < 4) {
                    if (mouseX > screenWidth / 2) {
                        tooltipTextWidth = mouseX - 12 - 8;
                    } else {
                        tooltipTextWidth = screenWidth - 16 - mouseX;
                    }
                    needsWrap = true;
                }
            }
            if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
                tooltipTextWidth = maxTextWidth;
                needsWrap = true;
            }
            if (needsWrap) {
                int wrappedTooltipWidth = 0;
                List<String> wrappedTextLines = new ArrayList<>();
                for (int i = 0; i < textLines.size(); i++) {
                    String textLine = textLines.get(i);
                    List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
                    if (i == 0) {
                        titleLinesCount = wrappedLine.size();
                    }
                    for (String line : wrappedLine) {
                        int lineWidth = font.getStringWidth(line);
                        if (lineWidth > wrappedTooltipWidth) {
                            wrappedTooltipWidth = lineWidth;
                        }
                        wrappedTextLines.add(line);
                    }
                }
                tooltipTextWidth = wrappedTooltipWidth;
                textLines = wrappedTextLines;
                if (mouseX > screenWidth / 2) {
                    tooltipX = mouseX - 16 - tooltipTextWidth;
                } else {
                    tooltipX = mouseX + 12;
                }
            }
            int tooltipY = mouseY - 12;
            int tooltipHeight = 8;
            if (textLines.size() > 1) {
                tooltipHeight += (textLines.size() - 1) * 10;
                if (textLines.size() > titleLinesCount) {
                    tooltipHeight += 2;
                }
            }
            if (tooltipY < 4) {
                tooltipY = 4;
            } else if (tooltipY + tooltipHeight + 4 > screenHeight) {
                tooltipY = screenHeight - tooltipHeight - 4;
            }
            final int zLevel = 300;
            int backgroundColor = 0xF0100010;
            int borderColorStart = 0x505000FF;
            int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
            RenderTooltipEvent.Color colorEvent = new RenderTooltipEvent.Color(stack, textLines, tooltipX, tooltipY, font, backgroundColor, borderColorStart, borderColorEnd);
            MinecraftForge.EVENT_BUS.post(colorEvent);
            backgroundColor = colorEvent.getBackground();
            borderColorStart = colorEvent.getBorderStart();
            borderColorEnd = colorEvent.getBorderEnd();
            drawGradientRect(zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
            drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
            drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            drawGradientRect(zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
            drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);
            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostBackground(stack, textLines, tooltipX, tooltipY, font, tooltipTextWidth, tooltipHeight));
            if (itemStack != stack) {
                inertia = 0;
                subtractY = 0;
                itemStack = stack;
            } else if (tooltipHeight <= screenHeight) {
                subtractY = 0;
            } else {
                inertia -= inertia / 20 - MathHelper.clamp(Mouse.getDWheel(), -2, 2);
                subtractY = MathHelper.clamp(subtractY + inertia, 0, tooltipHeight + (double) font.FONT_HEIGHT / 2- screenHeight);
            }
            int tooltipTop = tooltipY;
            for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
                String line = textLines.get(lineNumber);
                font.drawStringWithShadow(line, (float) tooltipX, (float) (tooltipY + subtractY), -1);
                if (lineNumber + 1 == titleLinesCount) {
                    tooltipY += 2;
                }
                tooltipY += 10;
            }
            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostText(stack, textLines, tooltipX, tooltipTop, font, tooltipTextWidth, tooltipHeight));
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }
}