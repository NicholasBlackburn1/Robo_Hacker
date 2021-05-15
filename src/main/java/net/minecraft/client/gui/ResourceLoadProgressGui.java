/***
 * opengl doesn't know gif
[3:57 PM]
but if you get the .gif image loaded as a series of frames
[3:57 PM]
you can use opengl to draw the gif
[3:57 PM]
but instead of opengl directly, I'd use minecraft's rendering code


https://medium.com/@andreshj87/drawing-a-gui-screen-on-minecraft-forge-7e0059015596
 */

package net.minecraft.client.gui;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import org.lwjgl.system.MemoryUtil;

import blackburn.BlackburnConst;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import org.apache.commons.compress.utils.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.stb.STBEasyFont;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.resources.IAsyncReloader;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Color;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.optifine.Config;
import net.optifine.reflect.Reflector;
import net.optifine.render.GlBlendState;
import net.optifine.shaders.config.ShaderPackParser;
import net.optifine.util.PropertiesOrdered;

public class ResourceLoadProgressGui extends LoadingGui {
    private static final ResourceLocation MOJANG_LOGO_TEXTURE = new ResourceLocation(
            "textures/gui/title/mojangstudios.png");
    private static final ResourceLocation NICKS_LOADING = new ResourceLocation("textures/gui/title/mojangstudios.png");
    private static final int field_238627_b_ = ColorHelper.PackedColor.packColor(255, 0, 0, 0);
    private static final int field_238628_c_ = field_238627_b_ & 16777215;
    private final Minecraft mc;
    private final IAsyncReloader asyncReloader;
    private final Consumer<Optional<Throwable>> completedCallback;
    private final boolean reloading;
    private float progress;
    private long fadeOutStart = -1L;
    private long fadeInStart = -1L;
    private int colorBackground = field_238628_c_;
    private int colorBar = ColorHelper.PackedColor.packColor(255, 0, 50, 61);
    private int colorOutline = 16777215;
    private int colorProgress = 16777215;
    private GlBlendState blendState = null;
    private boolean fadeOut = false;
    private static Texture forgeTexture;

    public ResourceLoadProgressGui(Minecraft p_i225928_1_, IAsyncReloader p_i225928_2_,
            Consumer<Optional<Throwable>> p_i225928_3_, boolean p_i225928_4_) {
        this.mc = p_i225928_1_;
        this.asyncReloader = p_i225928_2_;
        this.completedCallback = p_i225928_3_;
        this.reloading = false;
    }

    public static void loadLogoTexture(Minecraft mc) {
        mc.getTextureManager().loadTexture(MOJANG_LOGO_TEXTURE, new ResourceLoadProgressGui.MojangLogoTexture());
        forgeTexture =  mc.getTextureManager().loadmodTexture(NICKS_LOADING, new ResourceLoadProgressGui.NicksLogoTexture());
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    
        renderMojangLogo(matrixStack, mouseX, mouseY, partialTicks);
        
    }


    public void renderMojangLogo(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        FontRenderer render = this.mc.fontRenderer;
        int i = this.mc.getMainWindow().getScaledWidth();
        int j = this.mc.getMainWindow().getScaledHeight();
        long k = Util.milliTime();

        if (this.reloading && (this.asyncReloader.asyncPartDone() || this.mc.currentScreen != null)
                && this.fadeInStart == -1L) {
            this.fadeInStart = k;
        }

        float f = this.fadeOutStart > -1L ? (float) (k - this.fadeOutStart) / 1000.0F : -1.0F;
        float f1 = this.fadeInStart > -1L ? (float) (k - this.fadeInStart) / 500.0F : -1.0F;
        float f2;

        if (f >= 1.0F) {
            this.fadeOut = true;

            if (this.mc.currentScreen != null) {
                this.mc.currentScreen.render(matrixStack, 0, 0, partialTicks);
            }

            int l = MathHelper.ceil((1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
            fill(matrixStack, 0, 0, i, j, this.colorBackground | l << 24);
            f2 = 1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);
        } else if (this.reloading) {
            if (this.mc.currentScreen != null && f1 < 1.0F) {
                this.mc.currentScreen.render(matrixStack, mouseX, mouseY, partialTicks);
            }

            int i2 = MathHelper.ceil(MathHelper.clamp((double) f1, 0.15D, 1.0D) * 255.0D);
            fill(matrixStack, 0, 0, i, j, this.colorBackground | i2 << 24);
            f2 = MathHelper.clamp(f1, 0.0F, 1.0F);
        } else {
            fill(matrixStack, 0, 0, i, j, this.colorBackground | -16777216);
            f2 = 1.0F;
        }

        int j2 = (int) ((double) this.mc.getMainWindow().getScaledWidth() * 0.5D);
        int i1 = (int) ((double) this.mc.getMainWindow().getScaledHeight() * 0.5D);

        double d0 = Math.min((double) this.mc.getMainWindow().getScaledWidth() * 0.75D,
                (double) this.mc.getMainWindow().getScaledHeight()) * 0.25D;
        int j1 = (int) (d0 * 0.5D);
        double d1 = d0 * 4.0D;
        int k1 = (int) (d1 * 0.5D);

        this.mc.getTextureManager().bindTexture(NICKS_LOADING);
       
        RenderSystem.enableBlend();
        RenderSystem.blendEquation(32774);
        RenderSystem.blendFunc(770, 1);
        RenderSystem.alphaFunc(516, 0.0F);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, f2);

        boolean flag = true;

        if (this.blendState != null) {
            this.blendState.apply();

            if (!this.blendState.isEnabled() && this.fadeOut) {
                flag = false;
            }
        }

        if (flag) {
            blit(matrixStack, j2 - k1, i1 - j1 - 50, k1, (int) d0, -0.0625F, 0.0F, 120, 60, 120, 120);
            blit(matrixStack, j2, i1 - j1 - 50, k1, (int) d0, 0.0625F, 60.0F, 120, 60, 120, 120);

        }
        drawBox(200,200);
        RenderSystem.defaultBlendFunc();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.disableBlend();
        int l1 = (int) ((double) this.mc.getMainWindow().getScaledHeight() * 0.8325D);
        float f3 = this.asyncReloader.estimateExecutionSpeed();
        this.progress = MathHelper.clamp(this.progress * 0.95F + f3 * 0.050000012F, 0.0F, 1.0F);
        // gui.renderMessage("hello", 255F, 255, 1.0F);
        drawText();
        renderMemoryInfo();
        
        if (f < 1.0F) {
            this.func_238629_a_(matrixStack, i / 2 - k1, l1 - 5, i / 2 + k1, l1 + 5,
                    1.0F - MathHelper.clamp(f, 0.0F, 1.0F));
        }

        if (f >= 2.0F) {
            this.mc.setLoadingGui((LoadingGui) null);
        }

        if (this.fadeOutStart == -1L && this.asyncReloader.fullyDone() && (!this.reloading || f1 >= 2.0F)) {
            this.fadeOutStart = Util.milliTime();

            try {
                this.asyncReloader.join();
                this.completedCallback.accept(Optional.empty());
            } catch (Throwable throwable) {
                this.completedCallback.accept(Optional.of(throwable));
            }

            if (this.mc.currentScreen != null) {
                ScreenShotHelper.saveScreenshotDbg(mc.gameDir,mc.currentScreen.width ,mc.currentScreen.height,mc.getFramebuffer());
                this.mc.currentScreen.init(this.mc, this.mc.getMainWindow().getScaledWidth(),
                        this.mc.getMainWindow().getScaledHeight());
            }

        }
    }
    
    private void func_238629_a_(MatrixStack p_238629_1_, int p_238629_2_, int p_238629_3_, int p_238629_4_,
            int p_238629_5_, float p_238629_6_) {
        int i = MathHelper.ceil((float) (p_238629_4_ - p_238629_2_ - 2) * this.progress);
        int j = Math.round(p_238629_6_ * 255.0F);

        int j2 = this.colorOutline >> 16 & 255;
        int k2 = this.colorOutline >> 8 & 255;
        int l2 = this.colorOutline & 255;
        int i3 = ColorHelper.PackedColor.packColor(255, 0, 0, 0);
        fill(p_238629_1_, p_238629_2_ + 1, p_238629_3_, p_238629_4_ - 1, p_238629_3_ + 1, i3);
        fill(p_238629_1_, p_238629_2_ + 1, p_238629_5_, p_238629_4_ - 1, p_238629_5_ - 1, i3);
        fill(p_238629_1_, p_238629_2_, p_238629_3_, p_238629_2_ + 1, p_238629_5_, i3);
        fill(p_238629_1_, p_238629_4_, p_238629_3_, p_238629_4_ - 1, p_238629_5_, i3);
        i3 = ColorHelper.PackedColor.packColor(j, 0, 255, 30);
        fill(p_238629_1_, p_238629_2_ + 2, p_238629_3_ + 2, p_238629_2_ + i, p_238629_5_ - 2, i3);
    }

    public boolean isPauseScreen() {
        return true;
    }

    public void update() {
        this.colorBackground = field_238628_c_;
        this.colorBar = field_238628_c_;
        this.colorOutline = 16777215;
        this.colorProgress = 16777215;

        if (Config.isCustomColors()) {
            try {
                String s = "optifine/color.properties";
                ResourceLocation resourcelocation = new ResourceLocation(s);

                if (!Config.hasResource(resourcelocation)) {
                    return;
                }

                InputStream inputstream = Config.getResourceStream(resourcelocation);
                Config.dbg("Loading " + s);
                Properties properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                this.colorBackground = readColor(properties, "screen.loading", this.colorBackground);
                this.colorOutline = readColor(properties, "screen.loading.outline", this.colorOutline);
                this.colorBar = readColor(properties, "screen.loading.bar", this.colorBar);
                this.colorProgress = readColor(properties, "screen.loading.progress", this.colorProgress);
                this.blendState = ShaderPackParser.parseBlendState(properties.getProperty("screen.loading.blend"));
            } catch (Exception exception) {
                Config.warn("" + exception.getClass().getName() + ": " + exception.getMessage());
            }
        }
    }

    private static int readColor(Properties p_readColor_0_, String p_readColor_1_, int p_readColor_2_) {
        String s = p_readColor_0_.getProperty(p_readColor_1_);

        if (s == null) {
            return p_readColor_2_;
        } else {
            s = s.trim();
            int i = parseColor(s, p_readColor_2_);

            if (i < 0) {
                Config.warn("Invalid color: " + p_readColor_1_ + " = " + s);
                return i;
            } else {
                Config.dbg(p_readColor_1_ + " = " + s);
                return i;
            }
        }
    }

    private static int parseColor(String p_parseColor_0_, int p_parseColor_1_) {
        if (p_parseColor_0_ == null) {
            return p_parseColor_1_;
        } else {
            p_parseColor_0_ = p_parseColor_0_.trim();

            try {
                return Integer.parseInt(p_parseColor_0_, 16) & 16777215;
            } catch (NumberFormatException numberformatexception) {
                return p_parseColor_1_;
            }
        }
    }

    public boolean isFadeOut() {
        return this.fadeOut;
    }


    public void drawText(){
        float[] color = new float[] { 0.0f, 0.0f, 0.0f};
        memorycolour[0] = ((255 >> 16 ) & 0xFF) / 255.0f;
        color[0] = 16776960/255.0f;

        renderMessage("World Im here Yaa I Hacked Minecraft 1.16.5 UwU *Tail Wags in Excitement* ",color, ((mc.currentScreen.height - 15) / 10) -+ 2,  1.0f);
        renderMessage("By Nicholas Blackburn",color, ((mc.currentScreen.height - 15) / 10) -+ 1,  1.0f);
    }

    /**
     * This Fun is for Rendering Loading Screen Messages uwU
     */

    @SuppressWarnings("deprecation")
    public void renderMessage(final String message, final float[] colour, int line, float alpha) {
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
        RenderSystem.color3f(colour[0],colour[1],colour[2]);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(10, line * 10, 0);
        RenderSystem.scalef(1, 1, 0);
        RenderSystem.drawArrays(GL11.GL_QUADS, 0, quads * 4);
        RenderSystem.popMatrix();

        RenderSystem.enableCull();
        GlStateManager.disableClientState(GL11.GL_VERTEX_ARRAY);
        MemoryUtil.memFree(charBuffer);
    }

    /***
     * This is for trying to draw an custom Image on the Loading Page of Minecraft Granted U wont see it for Long 
     * Unless I add plugins to the Client UwU
     */
    @SuppressWarnings("deprecation")
    public void renderImageLogo(){

        // matrix setup
        int w = BlackburnConst.mc.getMainWindow().getWidth();
        int h = BlackburnConst.mc.getMainWindow().getHeight();
        float fw = 200.5f;
        float fh = 320.4f;
        glLoadIdentity();
        glOrtho(320 - w/2, 320 + w/2, 240 + h/2, 240 - h/2, -1, 1);
        glLoadIdentity();
        glColor4f(1, 1, 1, 1);
        

        glEnable(GL_TEXTURE_2D);
        forgeTexture.bindTexture();
        glColor3f(222, 222, 222);
        glBegin(GL_QUADS);
        glVertex2f(-fw, -fh);
    
        glVertex2f(-fw, fh);
       
        glVertex2f(fw, fh);

        glVertex2f(fw, -fh);
        glEnd();
        glDisable(GL_TEXTURE_2D);

                    // mojang logo
    }



    private void drawBox(int w, int h)
    {
        glBegin(GL_QUADS);
        glColor3ub((byte)((100 >> 16) & 0xFF), (byte)((100 >> 8) & 0xFF), (byte)(100 & 0xFF));
        glVertex2f(0, 0);
        glVertex2f(0, h);
        glVertex2f(w, h);
        glVertex2f(w, 0);
        glEnd();
    }


    private static final float[] memorycolour = new float[] { 0.0f, 0.0f, 0.0f};

    /**
     * This Is For Rendering Mem Info On Main Screen
     */
    private void renderMemoryInfo() {
        final MemoryUsage heapusage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        final MemoryUsage offheapusage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        final float pctmemory = (float) heapusage.getUsed() / heapusage.getMax();
        String memory = String.format("Memory Heap: %d / %d MB (%.1f%%)  OffHeap: %d MB", heapusage.getUsed() >> 20, heapusage.getMax() >> 20, pctmemory * 100.0, offheapusage.getUsed() >> 20);

        final int i = MathHelper.hsvToRGB((1.0f - (float)Math.pow(pctmemory, 1.5f)) / 3f, 1.0f, 0.5f);
        memorycolour[2] = ((i) & 0xFF) / 255.0f;
        memorycolour[1] = ((i >> 8 ) & 0xFF) / 255.0f;
        memorycolour[0] = ((i >> 16 ) & 0xFF) / 255.0f;
        renderMessage(memory, memorycolour, 1, 1.0f);
    }


        
    static class MojangLogoTexture extends SimpleTexture {
        public MojangLogoTexture() {
           super(ResourceLoadProgressGui.MOJANG_LOGO_TEXTURE);
        }
  
        protected SimpleTexture.TextureData getTextureImage(IResourceManager p_215246_1_) {
           Minecraft minecraft = Minecraft.getInstance();
           VanillaPack vanillapack = minecraft.getPackFinder().getVanillaPack();
  
           try (InputStream inputstream = vanillapack.getResourceStream(ResourcePackType.CLIENT_RESOURCES, ResourceLoadProgressGui.MOJANG_LOGO_TEXTURE)) {
              return new SimpleTexture.TextureData(new TextureMetadataSection(true, true), NativeImage.read(inputstream));
           } catch (IOException ioexception) {
              return new SimpleTexture.TextureData(ioexception);
           }
        }
    }
    
    static class NicksLogoTexture extends SimpleTexture {
        public NicksLogoTexture() {
           super(ResourceLoadProgressGui.NICKS_LOADING);
        }
  
        protected SimpleTexture.TextureData getTextureImage(IResourceManager p_215246_1_) {
           Minecraft minecraft = Minecraft.getInstance();
           VanillaPack vanillapack = minecraft.getPackFinder().getVanillaPack();
  
           try (InputStream inputstream = vanillapack.getResourceStream(ResourcePackType.CLIENT_RESOURCES, ResourceLoadProgressGui.NICKS_LOADING)) {
              return new SimpleTexture.TextureData(new TextureMetadataSection(true, true), NativeImage.read(inputstream));
           } catch (IOException ioexception) {
              return new SimpleTexture.TextureData(ioexception);
           }
        }
    }
}    