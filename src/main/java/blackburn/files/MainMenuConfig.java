package blackburn.files;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.util.PropertiesOrdered;
/***
 * Custom Config for Main Menu 
 * @author Nicholas Blakcburn
 */
public class MainMenuConfig {
    
    private static Calendar calendar = Calendar.getInstance();
    private static String outputBackground;
    

    // Allows User to Change Main Menu Background
    public static String updateMainMenuBackground()
    {
        calendar.setTime(new Date());
        IResourceManager iresourcemanager = Config.getResourceManager();

        if (iresourcemanager != null)
        {
            
            
            try
            {
                InputStream inputstream = iresourcemanager.getResource(new ResourceLocation("blackburn/MainMenu.properties")).getInputStream();
                
             

                Properties properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                String s = properties.getProperty("background");

                Config.warn(" got background image is"+"background: " + s);
                s = s.toLowerCase();

                if (s.isEmpty())
                {
                    Config.dbg("BACKGROUND KEY IS EMPTY LOADING DEFAULT IMAGE");
                    outputBackground = "textures/gui/title/background/background.png";
                }
                if(calendar.getTime().getMonth() == 12 && calendar.getTime().getDay() == 6){
                    Config.dbg("Happy Bday Nicholas Blackburn");
                    outputBackground = "textures/gui/title/background/Bday.png";
                    
                }
                if(calendar.getTime().getMonth() == 1 && calendar.getTime().getDay() == 1){
                    Config.dbg("Happy Bday Nicholas Blackburn");
                    outputBackground = "textures/gui/title/background/Bday.png";
                    
                }

                if(calendar.getTime().getMonth() == 4 && calendar.getTime().getDay() == 5){
                    Config.dbg("Hacked Client Creation hehe");
                    outputBackground = "textures/gui/title/background/Bday.png";
                    
                }  else{
                    outputBackground = s;
                }
               
            }
            catch (Exception exception)
            {
            }
           
        }
        return outputBackground;

    }
    
}
