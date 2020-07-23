package net.doubledoordev.oplock;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static net.doubledoordev.oplock.Oplock.MOD_ID;

@Config(modid = MOD_ID, category = "All")
@Mod.EventBusSubscriber(modid = MOD_ID)
public class ModConfig
{
    public static final ChatMessagesCauseYouCantTranslateAServerOnlyMod messages = new ChatMessagesCauseYouCantTranslateAServerOnlyMod();

    @Config.Comment("Default state the server is started in for OPLock.")
    public static Boolean defaultState = false;

    @Config.Comment("Anyone that has op and has a permission level greater than this is allowed to connect.")
    public static int allowedPermissionLevel = 1;

    @Config.Comment("Delay in ticks before the kick activates, If the world doesn't get to load the kick message won't show because the client is stupid.")
    public static int tickDelay = 30;

    @Config.Comment("Default message for clients that are rejected at login.")
    public static String loginDisconnectMessage = "This server is currently locked, Please try again later or contact an Admin for more info! - OpLock";

    @Config.Comment("Default kick message for players that are kicked from the server.")
    public static String kickDisconnectMessage = "This server has been locked, Please join later! - OpLock";

    @Config.Comment("Message sent to all online players notifying them of a server lock down.")
    public static String chatNotificationMessage = "<OpLock> The server has been locked, If you disconnect you will be unable to join again!";

    @Config.Comment("Should players that bypass the lock get a message on join telling them the current lock status?")
    public static Boolean chatJoinNotice = true;

    public static class ChatMessagesCauseYouCantTranslateAServerOnlyMod
    {
        @Config.Comment("Message sent people who bypass the lock on connect when the server is locked.")
        public String chatJoinMessageOn = "OpLock is ON!";

        @Config.Comment("Message sent people who bypass the lock on connect when the server is not locked.")
        public String chatJoinMessageOff = "OpLock is OFF!";

        @Config.Comment("Usage of the command.")
        public String commandUsage = "/oplock <true|false> <true|false> [message] | 1st arg: changes lock state, 2nd arg: should invalid users be kicked, 3rd optional: message sent for warning/kick\n";

        @Config.Comment("Status message")
        public String commandStatus = "OpLock is currently set to: ";

        @Config.Comment("Set status message")
        public String commandStatusSet = "OpLock status has been set to: ";
    }

    @Mod.EventBusSubscriber
    public static class SyncConfig
    {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if (event.getModID().equals(MOD_ID))
            {
                ConfigManager.sync(MOD_ID, Config.Type.INSTANCE);
            }
        }
    }

}
