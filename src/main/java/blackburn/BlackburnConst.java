package blackburn;
/**
 * Consts for my additions to mc 
 * @author Nicholas Blackburn
 */

import java.util.Calendar;

import blackburn.gui.GuiHackScreen;
import blackburn.gui.GuiNotify;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;

public class BlackburnConst {
    
    public static String VERSION = "1.0.1-";
    public static String Releasetag = "Beta-UWU";
    public static String Build = "DEBUG";

    public static String configFolder = "blackburn/resources";
    public static Boolean enableTrajectory = false;

    public static Minecraft mc = Minecraft.getInstance();
    public static GuiNotify note = new GuiNotify(mc.fontRenderer, mc);

    public static void enableTrajectory(boolean enable){
        enableTrajectory = enable;
    }


}
