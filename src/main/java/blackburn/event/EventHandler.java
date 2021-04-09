package blackburn.event;

import blackburn.BlackburnConst;
import blackburn.hacks.PlayerEsp;

public class EventHandler {
    

    // Registers Toogels to Minecraft instance
    public static void RegisterEvents(){
        PlayerButtonAction.toggleTrajectoryView();
        PlayerButtonAction.togglePlayerEsp();
        
    }

    public static void RegisterOnEnables(){
        BlackburnConst.esphack.onEnable();
        BlackburnConst.itemEsp.onEnable();
        
    }
}
