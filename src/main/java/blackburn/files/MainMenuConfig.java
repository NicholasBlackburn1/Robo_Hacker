package blackburn.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.lwjgl.system.CallbackI.S;

import blackburn.BlackburnConst;

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
    private static File configdir = new File(Minecraft.getInstance().gameDir+"/blackburn",null);
    private static File menuConfig = new File(Minecraft.getInstance().gameDir+"/blackburn","MainMenu.propertys");

    // Allows User to Change Main Menu Background
    public static String updateMainMenuBackground()
    {
        calendar.setTime(new Date());
        IResourceManager iresourcemanager =BlackburnConst.mc.getResourceManager();

        if (iresourcemanager != null)
        {
          
            try
            {
                if(!configdir.exists()){
                    configdir.mkdirs();
                    menuConfig.createNewFile();
                    menuConfig.setWritable(true);
                    writeConfigFileDefaults(menuConfig);
                }
                InputStream inputstream = new FileInputStream(menuConfig);
                
             

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
                    outputBackground = "textures/gui/title/background//Bday.png";
                    
                }

                if(calendar.getTime().getMonth() == 4 && calendar.getTime().getDay() == 5){
                    Config.dbg("Hacked Client Creation hehe");
                    outputBackground = "textures/gui/title/background//Bday.png";
                    
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


    private static void writeConfigFileDefaults(File input){
        try{
        FileWriter writer = new FileWriter(input);

        Config.warnblackburn("Writing to File the config!");

        writer.write("# Blackburn Main Menu Config File");
        writer.write("\n");
        writer.write("\n");
        writer.write("# Main menu Background Image");
        writer.write("background:");
        writer.close();
        Config.warnblackburn("Wrote config to the file!");
       

        }catch(Exception e){
            e.printStackTrace();
            Config.warnblackburn("failed to write config");

        }
    }

    
}
