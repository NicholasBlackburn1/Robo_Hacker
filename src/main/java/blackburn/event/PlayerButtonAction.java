package blackburn.event;

import blackburn.BlackburnConst;
import net.optifine.Config;

public class PlayerButtonAction {


    public static void toggleTrajectoryView(){
        
        if (BlackburnConst.mc.gameSettings.keybindTrajectoryHack.isKeyDown())
        {
            Config.warnblackburn("UwU going to render gui");
            BlackburnConst.tracking.enableDisplayOutput(true);
        

        } else{
            BlackburnConst.tracking.enableDisplayOutput(false);
        }
        

        

    }

    public static void togglePlayerEsp(){
        
        while (BlackburnConst.mc.gameSettings.keybindEsphack.isPressed())
        {
            Config.warnblackburn("UwU going to render Player Esp");
            BlackburnConst.esphack.onRender();
          
        } 
            BlackburnConst.esphack.onDisable();
        }
        
    }
    
}
