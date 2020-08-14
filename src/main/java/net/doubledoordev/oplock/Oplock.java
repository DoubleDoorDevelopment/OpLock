package net.doubledoordev.oplock;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.command.CommandHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


@Mod(
        modid = Oplock.MOD_ID,
        name = Oplock.MOD_NAME,
        version = Oplock.VERSION,
        serverSideOnly = true,
        acceptableRemoteVersions = "*"
)
public class Oplock
{

    public static final String MOD_ID = "oplock";
    public static final String MOD_NAME = "OpLock";
    public static final String VERSION = "1.0.2";
    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @Mod.Instance(MOD_ID)
    public static Oplock INSTANCE;
    public boolean serverLockStatus;
    public HashMap<UUID, Integer> delayedKickQueue = new HashMap<>();

    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        serverLockStatus = ModConfig.defaultState;
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * This is the second initialization event. Register custom recipes
     */
    @Mod.EventHandler
    public void serverStating(FMLServerStartingEvent event)
    {
        CommandHandler commandHandler = (CommandHandler) event.getServer().getCommandManager();
        commandHandler.registerCommand(new ServerLockCommand());
    }

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event)
    {
        EntityPlayer player = event.player;
        MinecraftServer serverInstace = FMLCommonHandler.instance().getMinecraftServerInstance();
        PlayerList playerList = serverInstace.getPlayerList();

        if (serverLockStatus && playerList.getOppedPlayers().getPermissionLevel(player.getGameProfile()) <= ModConfig.allowedPermissionLevel)
        {
            delayedKickQueue.put(player.getUniqueID(), serverInstace.getTickCounter() + ModConfig.tickDelay);
        }

        if (ModConfig.chatJoinNotice && playerList.getOppedPlayers().getPermissionLevel(player.getGameProfile()) >= ModConfig.allowedPermissionLevel)
        {
            if (serverLockStatus)
                player.sendMessage(new TextComponentString(ModConfig.messages.chatJoinMessageOn).setStyle(new Style().setColor(TextFormatting.RED)));
            else
                player.sendMessage(new TextComponentString(ModConfig.messages.chatJoinMessageOff).setStyle(new Style().setColor(TextFormatting.GREEN).setBold(true)));
        }
    }

    @SubscribeEvent
    public void tickHandler(TickEvent.ServerTickEvent event)
    {
        if (!delayedKickQueue.isEmpty() && event.phase == TickEvent.Phase.END)
        {
            MinecraftServer serverInstace = FMLCommonHandler.instance().getMinecraftServerInstance();
            PlayerList playerList = serverInstace.getPlayerList();

            int currentTick = serverInstace.getTickCounter();
            for (UUID uuid : delayedKickQueue.keySet())
            {
                EntityPlayerMP player = playerList.getPlayerByUUID(uuid);

                if (delayedKickQueue.get(uuid) <= currentTick && !player.hasDisconnected())
                {
                    player.connection.disconnect(new TextComponentString(ModConfig.loginDisconnectMessage));
                    delayedKickQueue.remove(uuid);
                }
            }
        }
    }
}
