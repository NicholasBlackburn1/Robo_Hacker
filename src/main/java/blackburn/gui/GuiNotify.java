package blackburn.gui;

import java.util.LinkedList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.util.text.ITextComponent;
import net.optifine.gui.GuiScreenOF;
import net.optifine.gui.TooltipManager;
import net.optifine.util.GuiPoint;
import net.optifine.util.GuiRect;
import net.optifine.util.GuiUtils;

public class GuiNotify{

    private FontRenderer renderer;
    private Minecraft mc;
    private long updateInfoLeftTimeMs = 0L;
    private List<String> notifyData = new LinkedList<String>();

    // This sets up method to set up diplay screen gui
    public GuiNotify(FontRenderer render, Minecraft mc){
        this.renderer = render;
        this.mc = mc;
      

    }

    /**
     * This Renders the display on screen for users
     */
    public void renderTextForUser(MatrixStack stacky){

        this.notifyData.add("Hest");
        this.notifyData.add("UWU");
        
        
        GuiPoint[] aguipoint = new GuiPoint[notifyData.size()];
        GuiRect[] aguirect = new GuiRect[notifyData.size()];

        for (int i = 0; i < notifyData.size(); ++i)
        {
            String s = notifyData.get(i);
            System.out.print(s);
            
            if (!notifyData.isEmpty())
            {
                int j = 9;
                int k = renderer.getStringWidth(s);
                int l = 2;
                int i1 = 2 + j * i;
                aguirect[i] = new GuiRect(1, i1 - 1, 2 + k + 1, i1 + j - 1);
                aguipoint[i] = new GuiPoint(2, i1);
            }
        }

        GuiUtils.fill(stacky.getLast().getMatrix(), aguirect, -1873784752);
        
        this.renderer.renderStrings(notifyData, aguipoint, 14737632, stacky.getLast().getMatrix(), false, this.renderer.getBidiFlag());
    }

}



 

