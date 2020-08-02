package net.doubledoordev.oplock;

import org.apache.commons.lang3.tuple.Pair;
import net.minecraftforge.common.ForgeConfigSpec;

public class OpLockConfig
{
    public static final General GENERAL;
    static final ForgeConfigSpec spec;

    static
    {
        final Pair<General, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(General::new);
        spec = specPair.getRight();
        GENERAL = specPair.getLeft();
    }

    public static class General
    {
        ForgeConfigSpec.IntValue allowedPermissionLevel;
        ForgeConfigSpec.IntValue tickDelay;

        ForgeConfigSpec.BooleanValue defaultState;
        ForgeConfigSpec.BooleanValue chatJoinNotice;

        ForgeConfigSpec.ConfigValue<String> loginDisconnectMessage;
        ForgeConfigSpec.ConfigValue<String> kickDisconnectMessage;
        ForgeConfigSpec.ConfigValue<String> chatOnNotificationMessage;
        ForgeConfigSpec.ConfigValue<String> chatOffNotificationMessage;
        ForgeConfigSpec.ConfigValue<String> chatJoinMessageOn;
        ForgeConfigSpec.ConfigValue<String> chatJoinMessageOff;
        ForgeConfigSpec.ConfigValue<String> commandUsage;
        ForgeConfigSpec.ConfigValue<String> commandStatus;
        ForgeConfigSpec.ConfigValue<String> commandStatusSet;
        ForgeConfigSpec.ConfigValue<String> kickedPlayerCountMessage;
        ForgeConfigSpec.ConfigValue<String> alreadyOn;

        General(ForgeConfigSpec.Builder builder)
        {
            builder.comment("General configuration settings")
                    .push("General");

            defaultState = builder
                    .comment("Default state the server is started in for OPLock.")
                    .define("defaultState", false);

            chatJoinNotice = builder
                    .comment("Should players that bypass the lock get a message on join telling them the current lock status?")
                    .define("chatJoinNotice", true);

            allowedPermissionLevel = builder
                    .comment("Anyone that has op and has a permission level greater than this is allowed to connect.")
                    .defineInRange("allowedPermissionLevel", 1, 0, Integer.MAX_VALUE);

            tickDelay = builder
                    .comment("Delay in ticks before the kick activates, If the world doesn't get to load the kick message won't show because the client is stupid.")
                    .defineInRange("tickDelay", 30, 0, Integer.MAX_VALUE);

            builder.pop();

            builder.comment("Translation and Customization strings.")
                    .push("Strings");

            loginDisconnectMessage = builder
                    .comment("Default message for clients that are rejected at login.")
                    .define("loginDisconnectMessage", "This server is currently locked, Please try again later or contact an Admin for more info! - OpLock");

            kickDisconnectMessage = builder
                    .comment("Default kick message for players that are kicked from the server.")
                    .define("kickDisconnectMessage", "This server has been locked, Please join later! - OpLock");

            chatOnNotificationMessage = builder
                    .comment("Message sent to all online players notifying them of a server lock down.")
                    .define("chatOnNotificationMessage", "<OpLock> The server has been locked, If you disconnect you will be unable to join again!");

            chatOffNotificationMessage = builder
                    .comment("Message sent to all online players notifying them the server open again.")
                    .define("chatOffNotificationMessage", "<OpLock> The server is open again!, You are free to reconnect as needed!");

            chatJoinMessageOn = builder
                    .comment("Message sent people who bypass the lock on connect when the server is locked.")
                    .define("chatJoinMessageOn", "OpLock is ON!");

            chatJoinMessageOff = builder
                    .comment("Message sent people who bypass the lock on connect when the server is not locked.")
                    .define("chatJoinMessageOff", "OpLock is OFF!");

            commandUsage = builder
                    .comment("Default message for clients that are rejected at login.")
                    .define("commandUsage", "This server is currently locked, Please try again later or contact an Admin for more info! - OpLock");

            commandStatus = builder
                    .comment("Status message.")
                    .define("commandStatus", "OpLock is currently set to: ");

            commandStatusSet = builder
                    .comment("Set status message.")
                    .define("commandStatusSet", "OpLock status has been set to: ");

            kickedPlayerCountMessage = builder
                    .comment("Info message for amount of users kicked.")
                    .define("kickedPlayerCountMessage", "# of players kicked from server: ");

            alreadyOn = builder
                    .comment("Info message for if OpLock is already on.")
                    .define("alreadyOn", "OpLock is already on!");
        }
    }
}
