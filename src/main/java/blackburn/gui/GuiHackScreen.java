package blackburn.gui;


import java.util.Calendar;
import java.util.Date;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import blackburn.BlackburnConst;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.optifine.Config;
import net.optifine.config.ConfigUtils;
import net.optifine.gui.GuiButtonOF;
import net.optifine.gui.GuiScreenOF;
import net.optifine.gui.TooltipManager;
import net.optifine.gui.TooltipProviderOptions;

public class GuiHackScreen extends GuiScreenOF
{   
    private Calendar calendar = Calendar.getInstance();
    private Screen prevScreen;
    private GameSettings settings;
    private String splasher;
    private String Date;
    private Button arrowtracking;
    
   // private static AbstractOption[] enumOptions = new AbstractOption[] {AbstractOption.CLOUDS, AbstractOption.CLOUD_HEIGHT, AbstractOption.TREES, AbstractOption.RAIN, AbstractOption.SKY, AbstractOption.STARS, AbstractOption.SUN_MOON, AbstractOption.SHOW_CAPES, AbstractOption.FOG_FANCY, AbstractOption.FOG_START, AbstractOption.TRANSLUCENT_BLOCKS, AbstractOption.HELD_ITEM_TOOLTIPS, AbstractOption.DROPPED_ITEMS, AbstractOption.SWAMP_COLORS, AbstractOption.VIGNETTE, AbstractOption.ALTERNATE_BLOCKS, AbstractOption.ENTITY_DISTANCE_SCALING, AbstractOption.BIOME_BLEND_RADIUS};
    private TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());

    public GuiHackScreen()
    {
        super(new StringTextComponent(I18n.format("blackburn.about")));
        
    }

    public void init()
    {   
       
        calendar.setTime(new Date());
        
        Date = calendar.getTime().toString();
        splasher = this.minecraft.getSplashes().getSplashText();
        this.addButton(new GuiButtonOF(200, this.width / 2 - 100, this.height / 6 + 200 + 11, I18n.format("gui.done")));
        this.addButton(new GuiButtonOF(10, this.width / 2 - 100, this.height / 6 + 100 + 11, I18n.format("gui.trajectory")));
    
    }

    protected void actionPerformed(Widget guiElement)
    {
        if (guiElement instanceof GuiButtonOF)
        {
            GuiButtonOF guibuttonof = (GuiButtonOF)guiElement;

            if (guibuttonof.active)
            {
                if (guibuttonof.id == 200)
                {
                    this.minecraft.gameSettings.saveOptions();
                    this.minecraft.displayGuiScreen(this.prevScreen);
                }
                if(guibuttonof.keyPressed(10, 10, 0)){
                    BlackburnConst.setTrajectEnable();
                    Config.warnblackburn("PressedButton");
                }
            }
        }
    }

    public void onClose()
    {
        this.minecraft.gameSettings.saveOptions();
        super.onClose();
    }

    public void render(MatrixStack matrixStackIn, int x, int y, float partialTicks)
    {
      
        this.renderBackground(matrixStackIn);
        drawCenteredString(matrixStackIn, this.minecraft.fontRenderer, this.title, this.width / 2, 15, 16777215);

        this.drawCenteredString(matrixStackIn, this.font, splasher,this.width/2 ,30, 16776960);
        
        

        super.render(matrixStackIn, x, y, partialTicks);
        this.tooltipManager.drawTooltips(matrixStackIn, x, y, this.buttonList);
    }
}