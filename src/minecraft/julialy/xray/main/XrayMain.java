package julialy.xray.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import julialy.xray.GuiXRAYMain;

import org.lwjgl.input.Keyboard;

import com.mumfrey.liteloader.util.ModUtilities;

import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.src.*;

public class XrayMain {
	
	public static XrayMain currentInstance;
	
    public Minecraft mc;
    public static boolean on = false;
    public static boolean cavefinder = false;
    public static int lightmode = 0;
    public static int[] blackList = new int[] {1, 2, 3, 4, 7, 12, 13, 24, 87, 8, 9, 78, 79, 80};
    public KeyBinding[] keys = new KeyBinding[4];
    public static String nazwa = "";
    public static String versionOfMod = "1.5.2";
    
    //TODO configuration API
    public String currentSelectedProfile = "xrayProfile1";
    
    public XrayMain() {
    	checkififindmyclasses();
    	nazwa = currentSelectedProfile + ".xrayprofile";
        mc = Minecraft.getMinecraft();
        loadBlackList(nazwa);
        createBlacklistFolder();
        XrayMain.printLineInLog("Creating instance, please wait..." );
        keys[0] = new KeyBinding("[X-ray] X-RAY", Keyboard.KEY_X);
        keys[1] = new KeyBinding("[X-ray] Brightness", Keyboard.KEY_C);
        keys[2] = new KeyBinding("[X-ray] Cave Finder", Keyboard.KEY_V);
        //System.out.println("Please ignore the following messages that appear."); //now un needed
        //ModLoader.setInGameHook(this, true, false); //is this the "game activity during mod construction"?
        //ModLoader.setInGUIHook(this, true, false);
        for (KeyBinding key : this.keys)
        {
        	if (key != null) { 
        		ModUtilities.registerKey(key);
        	}
        }
        XrayMain.printLineInLog("X-RAY Loading Complete!");
    }
    
    public static void loadModXray() {
    	XrayMain.printLineInLog("X-ray is currently installed! Version: " + versionOfMod );
    	currentInstance = new XrayMain();
    }
    
    public static XrayMain getXrayInstance() {
    	if (currentInstance == null) {
    		loadModXray();
    	}
    	return currentInstance;
    }
    
    public void onLoginToServer() {
    	addChatMessage("Thank you for using Julialy's X-RAY mod!");
    	addChatMessage("You will find the latest version here:\2471\247n http://goo.gl/auOLG");
    }
    
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
    	if (minecraft.inGameHasFocus) {
    		if (inGame) {
    			if (this.keys[0].isPressed()) {
                    if (!Keyboard.isKeyDown(29) && !Keyboard.isKeyDown(157))
                    {
                    	XrayMain.printLineInLog("Toggling X-ray...");
                        on = !on;
                        cavefinder = false;
                        rerendereverything(true);
                    }
                    else
                    {
                    	XrayMain.printLineInLog("Opening GuiXRAYMain.");
                        mc.displayGuiScreen(new GuiXRAYMain(null, mc.gameSettings));
                    }
    			}
    			if (this.keys[1].isPressed()) {
    				XrayMain.printLineInLog("Toggling nightlight...");
                    ++lightmode;
                    if (lightmode >= 3)
                    {
                        lightmode = 0;
                        disableBrightLight();
                        mc.entityRenderer.updateRenderer();
                    }
                    if (lightmode >= 1) {
                        mc.entityRenderer.updateRenderer();
                        if (lightmode == 2) {
                        	enableBrightLight();
                        }
                    }
    			}
    			if (this.keys[2].isPressed()) {
    				XrayMain.printLineInLog("Toggling cavefinder...");
                    cavefinder = !cavefinder;
                    on = false;
                    rerendereverything(true);
    			}

    		}
    	}
    }
    
   public void disableBrightLight() {
    	//mc.thePlayer.removePotionEffect( 16 );
    	mc.gameSettings.gammaSetting = 0.2F;
    }

    public void enableBrightLight() {
    	//mc.thePlayer.addPotionEffect( new PotionEffect( 16, 99999999, 255, true ) );
    	mc.gameSettings.gammaSetting = 782;
    }
    
    public void loadBlackList(String var0)
    {
    	XrayMain.printLineInLog("LOADING BLACKLIST: " + var0 + " is now loading...");
        try
        {
            int[] var1 = new int[512];
            File var2 = new File(mc.mcDataDir.getPath() + File.separator + "xRayProfiles", var0);

            if (var2.exists())
            {
                blackList = null;
                @SuppressWarnings("resource")
				BufferedReader var3 = new BufferedReader(new FileReader(var2));
                int var4;
                String var5;

                for (var4 = 0; (var5 = var3.readLine()) != null; ++var4)
                {
                    var1[var4] = Integer.parseInt(var5);
                }

                blackList = new int[var4];
                System.arraycopy(var1, 0, blackList, 0, var4);
            }
        }
        catch (Exception var6)
        {
        	XrayMain.printLineInLog("COULD NOT LOAD BLACKLIST: " + var0);
            var6.printStackTrace();
        }
    }

    public void saveBlackList(String var0)
    {
    	XrayMain.printLineInLog("SAVING BLACKLIST: " + var0 + " is now saving...");
        try
        {
            File var1 = new File(mc.mcDataDir.getPath() + File.separator + "xRayProfiles", var0); //Their is no seperator when calling the locator directory, so add one myself!!!!!
            BufferedWriter var2 = new BufferedWriter(new FileWriter(var1));
            int[] var3 = blackList;
            int var4 = blackList.length;

            for (int var5 = 0; var5 < var4; ++var5)
            {
                int var6 = var3[var5];
                var2.write(var6 + "\r\n");
            }

            var2.close();
        }
        catch (Exception var7)
        {
        	XrayMain.printLineInLog("COULD NOT SAVE BLACKLIST: " + var0);
            var7.printStackTrace();
        }
    }
    
    public void createBlacklistFolder() {
    	XrayMain.printLineInLog("CREATING BLACKLIST FOLDER...");
        try
        {
            File var1 = new File(mc.mcDataDir, "xRayProfiles"); //Their is no seperator when calling the locator directory, so add one myself!!!!!
            boolean canMakeDir = var1.mkdir();
            if (canMakeDir == false) {
            	XrayMain.printLineInLog("COULD NOT CREATE BLACKLIST FOLDER: Java could not make this folder for some reason, it may already exist");
            }
        }
        catch (Exception var7)
        {
        	XrayMain.printLineInLog("COULD NOT CREATE BLACKLIST FOLDER: An crash ocoured");
            var7.printStackTrace();
        }
    }
    
    public void rerendereverything(Boolean thisisntagenericvar) {
    	if (thisisntagenericvar == true) {
    		mc.renderGlobal.loadRenderers();
    	}
        mc.entityRenderer.updateRenderer();
	}
    
    public void loadBlackListName(String iknowthisisntagoodvar) {
    	saveBlackList(nazwa);
    	currentSelectedProfile = iknowthisisntagoodvar;
    	nazwa = currentSelectedProfile + ".xrayprofile";
    	loadBlackList(nazwa);
	}
    
    public void saveCurrentBlackList() {
    	saveBlackList(nazwa);
    }
    
    public void checkififindmyclasses()
    {
    	try
    	{
    	/*
    	Block.doesXrayStuffExist();
    	BlockFluid.doesXrayStuffExist();
    	Entity.doesXrayStuffExist();
    	*/
    	}
        catch (Exception var6)
        {
        	//TODO: Check for forge and if it exists, kill Minecraft and add a notice that Forge is a virus xD
        	XrayMain.printLineInLog("Oh fuck! Some of my class files are missing! Where are they?!");
        	XrayMain.printLineInLog("This X-Ray mod could not find some of it's class files.");
        	XrayMain.printLineInLog("Some functions may not work correctly.");
        	XrayMain.printLineInLog("Be sure you put this mod in the minecraft.jar and not in the mods folder!");
        	XrayMain.printLineInLog("If you did do that, one of your mods are conflicting!");
        }
    	//TODO: If this mod is on LexManos system then cause a show a video and then crash
    	XrayMain.LexManos_sucks_like_a_dick();
    }
    
    public static int shouldXraySideBeRendered(int blockID) {


        int[] var6;
        int var7;
        int var8;
        int var9;

        if (XrayMain.on)
        {
            var6 = XrayMain.blackList;
            var7 = var6.length;

            for (var8 = 0; var8 < var7; ++var8)
            {
                var9 = var6[var8];

                if (var9 == blockID)
                {
                    return 1; //false
                }
            }

            if (XrayMain.blackList.length != 0)
            {
                return 2; //true
            }
        }
        else if (XrayMain.cavefinder)
        {
            var6 = XrayMain.blackList;
            var7 = var6.length;

            for (var8 = 0; var8 < var7; ++var8)
            {
                var9 = var6[var8];

                if (var9 != 1 && var9 == blockID)
                {
                    return 1; //false
                }
            }
        }


        return 0; //null
    }

    public static void printLineInLog(String linelolol) {
    	System.out.println("[X-ray] " + linelolol);
    }
    
	public void addChatMessage(String eatmyasslexmanos) {
		String toSend = "\2474" + "[X-RAY] " + "\247r" + eatmyasslexmanos;
		mc.thePlayer.addChatMessage(toSend);
	}
    
    
    public static boolean processIntToBoolean(int number) {
    	if (number == 2) {
    		return true;
    	}
    	return false;
    }
    
	public static void LexManos_sucks_like_a_dick() {
		 if (ClientBrandRetriever.getClientModName().contains("fml")) {
			 XrayMain.printLineInLog("[WARNING] LexManos sucks like a fucking bloody ass!");
			 XrayMain.printLineInLog("[WARNING] This mod may not work because you are using Forge!");
			 XrayMain.printLineInLog("[WARNING] No support will be given for people using Forge.");
			 XrayMain.printLineInLog("[WARNING] However, sometimes this mod may work with Forge!");
			 XrayMain.printLineInLog("[WARNING] Please don't complain about this message!");
		 }
		 else {
			 XrayMain.printLineInLog("[INFO] Good job! You are not using Forge.");
			 XrayMain.printLineInLog("[INFO] If you need any support, ask on this mod's MinecraftForums topic.");
		 }
		 XrayMain.printLineInLog("");
		 XrayMain.printLineInLog("  ______                     _        __                 _ _      _        ");
		 XrayMain.printLineInLog(" |  ____|                   (_)      / _|               | (_)    | |       ");
		 XrayMain.printLineInLog(" | |__ ___  _ __ __ _  ___   _ ___  | |_ ___  _ __    __| |_  ___| | _____ ");
		 XrayMain.printLineInLog(" |  __/ _ \\| '__/ _` |/ _ \\ | / __| |  _/ _ \\| '__|  / _` | |/ __| |/ / __|");
		 XrayMain.printLineInLog(" | | | (_) | | | (_| |  __/ | \\__ \\ | || (_) | |    | (_| | | (__|   <\\__ \\");
		 XrayMain.printLineInLog(" |_|  \\___/|_|  \\__, |\\___| |_|___/ |_| \\___/|_|     \\__,_|_|\\___|_|\\_\\___/");
		 XrayMain.printLineInLog("                 __/ |                                                     ");
		 XrayMain.printLineInLog("                |___/                                                      ");
		 XrayMain.printLineInLog("");
		 XrayMain.printLineInLog("  ______          _                            _               __  __                       _ ");
		 XrayMain.printLineInLog(" |  ____|        | |                          | |             |  \\/  |                     | |");
		 XrayMain.printLineInLog(" | |__ _   _  ___| | __  _   _  ___  _   _    | |     _____  _| \\  / | __ _ _ __   ___  ___| |");
		 XrayMain.printLineInLog(" |  __| | | |/ __| |/ / | | | |/ _ \\| | | |   | |    / _ \\ \\/ / |\\/| |/ _` | '_ \\ / _ \\/ __| |");
		 XrayMain.printLineInLog(" | |  | |_| | (__|   <  | |_| | (_) | |_| |_  | |___|  __/>  <| |  | | (_| | | | | (_) \\__ \\_|");
		 XrayMain.printLineInLog(" |_|   \\__,_|\\___|_|\\_\\  \\__, |\\___/ \\__,_( ) |______\\___/_/\\_\\_|  |_|\\__,_|_| |_|\\___/|___(_)");
		 XrayMain.printLineInLog("                          __/ |           |/                                                  ");
		 XrayMain.printLineInLog("                         |___/                                                                ");
		 XrayMain.printLineInLog("");
		 XrayMain.printLineInLog("This message provided to you by julialy");
		 XrayMain.printLineInLog("");
	}

}
