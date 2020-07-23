package net.doubledoordev.oplock;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class ServerLockCommand extends CommandBase
{
    @Override
    public String getName()
    {
        return "oplock";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return ModConfig.messages.commandUsage;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        switch (args.length)
        {
            case 0:
                sender.sendMessage(new TextComponentString(ModConfig.messages.commandStatus).appendText(String.valueOf(Oplock.INSTANCE.serverLockStatus).toUpperCase()));
                break;
            case 1:
                Oplock.INSTANCE.serverLockStatus = parseBoolean(args[0].toLowerCase());
                sender.sendMessage(new TextComponentString(ModConfig.messages.commandStatusSet).appendText(args[0].toUpperCase()));
                break;
            case 2:
                messageAndKick(server, sender, parseBoolean(args[0].toLowerCase()), parseBoolean(args[1].toLowerCase()), null);
                break;
            default:
                messageAndKick(server, sender, parseBoolean(args[0].toLowerCase()), parseBoolean(args[1].toLowerCase()), new TextComponentString(buildString(args, 2)));
        }
    }

    private void messageAndKick(MinecraftServer server, ICommandSender sender, boolean lockState, boolean shouldKick, ITextComponent message)
    {
        Oplock.INSTANCE.serverLockStatus = lockState;

        if (lockState)
            for (EntityPlayerMP player : server.getPlayerList().getPlayers())
            {
                if (shouldKick && server.getPlayerList().getOppedPlayers().getPermissionLevel(player.getGameProfile()) < ModConfig.allowedPermissionLevel)
                    if (message != null)
                        player.connection.disconnect(message);
                    else
                        player.connection.disconnect(new TextComponentString(ModConfig.kickDisconnectMessage).setStyle(new Style().setColor(TextFormatting.RED).setBold(true)));

                player.sendMessage(new TextComponentString(ModConfig.chatNotificationMessage).setStyle(new Style().setColor(TextFormatting.RED).setBold(true)));
                if (message != null)
                {
                    player.sendMessage(new TextComponentString("<OpLock> ").setStyle(new Style().setColor(TextFormatting.AQUA).setBold(true)).appendSibling(message));
                }
            }

        sender.sendMessage(new TextComponentString(ModConfig.messages.commandStatusSet).appendText(String.valueOf(lockState).toUpperCase()));
    }
}
