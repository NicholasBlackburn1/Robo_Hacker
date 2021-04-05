package blackburn.files;

import java.io.InputStream;
import java.util.Properties;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.util.PropertiesOrdered;

public class MainMenuConfig {
    
    

    // Allows User to Change Main Menu Background
    public static String updateMainMenuBackground()
    {
        
        IResourceManager iresourcemanager = Config.getResourceManager();

        if (iresourcemanager != null)
        {
            try
            {
                InputStream inputstream = iresourcemanager.getResource(new ResourceLocation("blackburn/MainMenu.properties")).getInputStream();

                if (inputstream == null)
                {
                    return;
                }

                Properties properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                String s = properties.getProperty("background");

                Config.warn(" got background image is"+"background: " + s);
                s = s.toLowerCase();

                if (s.isEmpty())
                {
                    Config.dbg("BACKGROUND KEY IS EMPTY LOADING DEFAULT IMAGE");
                    return "textures/gui/title/background/background.png";
                } 
                

            }
            catch (Exception exception)
            {
            }
        }

    }

    private void cycleThroughBackgrounds(){
        
    }
}
