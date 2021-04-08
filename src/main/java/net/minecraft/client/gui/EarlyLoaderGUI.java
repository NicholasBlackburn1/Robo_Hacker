package net.minecraft.client.gui;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.stb.STBEasyFont;
import org.lwjgl.system.MemoryUtil;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;


public class EarlyLoaderGUI {
    private final MainWindow window;
    private boolean handledElsewhere;

    public EarlyLoaderGUI(final MainWindow window) {
        this.window = window;
    }

    @SuppressWarnings("deprecation")
    private void setupMatrix() {
        RenderSystem.clear(256, Minecraft.IS_RUNNING_ON_MAC);
        RenderSystem.matrixMode(5889);
        RenderSystem.loadIdentity();
        RenderSystem.ortho(0.0D, window.getWidth() / window.getGuiScaleFactor(), window.getHeight() / window.getGuiScaleFactor(), 0.0D, 1000.0D, 3000.0D);
        RenderSystem.matrixMode(5888);
        RenderSystem.loadIdentity();
        RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
    }

    public void handleElsewhere() {
        this.handledElsewhere = true;
    }

    public void renderFromGUI(String mes) {
        renderMessages(mes);
    }

    @SuppressWarnings("deprecation")
    void renderTick(String msg) {
        if (handledElsewhere) return;
        int guiScale = window.calcGuiScale(0, false);
        window.setGuiScale(guiScale);

        RenderSystem.clearColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);
        RenderSystem.pushMatrix();
        setupMatrix();
        renderBackground();
        renderMessages(msg);
        window.updateDisplay();
        RenderSystem.popMatrix();
    }

    private void renderBackground() {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor4f(239F / 255F, 50F / 255F, 61F / 255F, 255F / 255F); //Color from ResourceLoadProgressGui
        GL11.glVertex3f(0, 0, -10);
        GL11.glVertex3f(0, window.getScaledHeight(), -10);
        GL11.glVertex3f(window.getScaledWidth(), window.getScaledHeight(), -10);
        GL11.glVertex3f(window.getScaledWidth(), 0, -10);
        GL11.glEnd();
    }

    private void renderMessages(String messages) {
        
        for (int i = 0; i < 10; i++) {
            boolean nofade = i == 0;
            ;
            final float fade = MathHelper.clamp((4000.0f  - ( i - 4 ) * 1000.0f) / 5000.0f, 0.0f, 1.0f);
            if (fade <0.01f && !nofade) continue;
           
            renderMessage(messages, 255F, ((window.getScaledHeight() - 15) / 10) - i + 1, nofade ? 1.0f : fade);
        }
      
    }

    private static final float[] memorycolour = new float[] { 0.0f, 0.0f, 0.0f};


    @SuppressWarnings("deprecation")
    void renderMessage(final String message, final float f, int line, float alpha) {
        GlStateManager.enableClientState(GL11.GL_VERTEX_ARRAY);
        ByteBuffer charBuffer = MemoryUtil.memAlloc(message.length() * 270);
        int quads = STBEasyFont.stb_easy_font_print(0, 0, message, null, charBuffer);
        GL14.glVertexPointer(2, GL11.GL_FLOAT, 16, charBuffer);

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        // STBEasyFont's quads are in reverse order or what OGGL expects, so it gets culled for facing the wrong way.
        // So Disable culling https://github.com/MinecraftForge/MinecraftForge/pull/6824
        RenderSystem.disableCull();
        GL14.glBlendColor(0,0,0, alpha);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.CONSTANT_ALPHA, GlStateManager.DestFactor.ONE_MINUS_CONSTANT_ALPHA);
        GL11.glColor4f(239F / 255F, 50F / 255F, 61F / 255F, 255F / 255F); //Color from ResourceLoadProgressGui
        RenderSystem.pushMatrix();
        RenderSystem.translatef(10, line * 10, 0);
        RenderSystem.scalef(1, 1, 0);
        RenderSystem.drawArrays(GL11.GL_QUADS, 0, quads * 4);
        RenderSystem.popMatrix();

        RenderSystem.enableCull();
        GlStateManager.disableClientState(GL11.GL_VERTEX_ARRAY);
        MemoryUtil.memFree(charBuffer);
    }
}
