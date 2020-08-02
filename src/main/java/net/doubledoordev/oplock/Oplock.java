package net.doubledoordev.oplock;

import java.util.HashMap;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.OpEntry;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("oplock")
public class Oplock
{

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static Oplock INSTANCE;
    public boolean serverLockStatus;
    public HashMap<UUID, Integer> delayedKickQueue = new HashMap<>();

    public Oplock()
    {

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        INSTANCE = this;
    }

    @SubscribeEvent
    public void serverStarting(FMLServerStartingEvent event)
    {
        CommandOpLock.register(event.getCommandDispatcher());
    }

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event)
    {
        PlayerEntity player = event.getPlayer();
        MinecraftServer serverInstace = ServerLifecycleHooks.getCurrentServer();
        OpEntry opEntry = serverInstace.getPlayerList().getOppedPlayers().getEntry(player.getGameProfile());

        if (serverLockStatus && opEntry != null && opEntry.getPermissionLevel() <= OpLockConfig.GENERAL.allowedPermissionLevel.get())
        {
            delayedKickQueue.put(player.getUniqueID(), serverInstace.getTickCounter() + OpLockConfig.GENERAL.tickDelay.get());
        }

        if (OpLockConfig.GENERAL.chatJoinNotice.get() && opEntry != null && opEntry.getPermissionLevel() >= OpLockConfig.GENERAL.allowedPermissionLevel.get())
        {
            if (serverLockStatus)
                player.sendMessage(new StringTextComponent(OpLockConfig.GENERAL.chatJoinMessageOn.get()).setStyle(new Style().setColor(TextFormatting.RED)));
            else
                player.sendMessage(new StringTextComponent(OpLockConfig.GENERAL.chatJoinMessageOff.get()).setStyle(new Style().setColor(TextFormatting.GREEN).setBold(true)));
        }
    }

    @SubscribeEvent
    public void tickHandler(TickEvent.ServerTickEvent event)
    {
        if (!delayedKickQueue.isEmpty() && event.phase == TickEvent.Phase.END)
        {
            MinecraftServer serverInstace = ServerLifecycleHooks.getCurrentServer();
            PlayerList playerList = serverInstace.getPlayerList();

            int currentTick = serverInstace.getTickCounter();
            for (UUID uuid : delayedKickQueue.keySet())
            {
                PlayerEntity player = playerList.getPlayerByUUID(uuid);

                if (delayedKickQueue.get(uuid) <= currentTick)
                {
                    ServerPlayerEntity serverPlayer = null;
                    if (player != null)
                        serverPlayer = playerList.getPlayerByUUID(player.getUniqueID());
                    if (serverPlayer != null)
                    {
                        serverPlayer.connection.disconnect(new StringTextComponent(OpLockConfig.GENERAL.loginDisconnectMessage.get()));
                    }
                    delayedKickQueue.remove(uuid);
                }
            }
        }
    }
}
