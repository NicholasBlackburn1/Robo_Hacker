package blackburn.event;

import blackburn.BlackburnConst;
import blackburn.hacks.PlayerEsp;

public class EventHandler {
    

    // Registers Toogels to Minecraft instance
    public static void RegisterEvents(){
        PlayerButtonAction.toggleTrajectoryView();
        PlayerButtonAction.togglePlayerEsp();
        PlayerButtonAction.toggleItemEsp();
        
    }

    public static void RegisterOnEnables(){
        BlackburnConst.esphack.onEnable();
        BlackburnConst.itemEsp.onEnable();
        
    }

    public static void RegisterRenderEvents(){
        BlackburnConst.esphack.onRender();
        BlackburnConst.itemEsp.onRender();
    }

    public static void RegisterUpdateEvents(){
        BlackburnConst.itemEsp.onUpdate();
        BlackburnConst.esphack.onUpdate();
    }

    public static void RegisterParticleTics(float ticks){
        BlackburnConst.esphack.setParticalTicks(ticks);
        BlackburnConst.itemEsp.setParticleTics(ticks);
    }

}
