package blackburn.event;

import blackburn.BlackburnConst;
import net.optifine.Config;

public class PlayerButtonAction {


    public static void toggleTrajectoryView(){
        /*
        if (BlackburnConst.mc.gameSettings.keybindTrajectoryHack.isKeyDown())
        {
            Config.warnblackburn("UwU going to render gui");
            BlackburnConst.tracking.enableDisplayOutput(true);
        

        } else{
            BlackburnConst.tracking.enableDisplayOutput(false);
        }
        
        */
        

    }

    public static void toggleCustomDisplay(){
        
        if (BlackburnConst.mc.gameSettings.keybindEsphack.isPressed())
        {   
            Config.warnblackburn("UwU going to render Cusotm data display");
            BlackburnConst.enableTrajectory(true);
            
        }else{
            BlackburnConst.enableTrajectory(false);
        }
        
        
    }

    public static void toggleItemEsp(){
        /*
        if (BlackburnConst.mc.gameSettings.keybindItemEspHack.isPressed())
        {   
            Config.warnblackburn("UwU going to render item Esp");
            
            BlackburnConst.itemEsp.setEnabled(true);
        }else{
            BlackburnConst.itemEsp.setEnabled(false);
        }
        */
    }
}
    
