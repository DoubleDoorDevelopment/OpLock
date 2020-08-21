package net.doubledoordev.oplock;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.OpEntry;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class CommandOpLock
{
    private static final SimpleCommandExceptionType ALREADY_ON = new SimpleCommandExceptionType(new StringTextComponent(OpLockConfig.GENERAL.alreadyOn.get()));

    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("oplock")
                .requires((sender) -> sender.hasPermissionLevel(3)
                )
                .executes(((sender) ->
                        getStatus(sender.getSource()))
                )
                .then(Commands.literal("on")
                        .executes((sender) ->
                                turnOnOpLock(sender.getSource(), false, new StringTextComponent(OpLockConfig.GENERAL.kickDisconnectMessage.get()), null)
                        )
                        .then(Commands.argument("kick", BoolArgumentType.bool())
                                .executes((sender) ->
                                        turnOnOpLock(sender.getSource(), BoolArgumentType.getBool(sender, "kick"), new StringTextComponent(OpLockConfig.GENERAL.kickDisconnectMessage.get()), null
                                        )
                                )
                                .then(Commands.argument("message", MessageArgument.message())
                                        .executes((sender) ->
                                                turnOnOpLock(sender.getSource(), BoolArgumentType.getBool(sender, "kick"), null, MessageArgument.getMessage(sender, "message"))
                                        )
                                )
                        )
                )
                .then(Commands.literal("off")
                        .executes((sender) ->
                                turnOffOpLock(sender.getSource())
                        )
                )
                .then(Commands.literal("kick")
                        .executes((sender) ->
                                kickInvalidPlayers(sender.getSource(), new StringTextComponent(OpLockConfig.GENERAL.kickDisconnectMessage.get()))
                        )
                        .then(Commands.argument("message", MessageArgument.message())
                                .executes((sender) ->
                                        kickInvalidPlayers(sender.getSource(), MessageArgument.getMessage(sender, "message"))
                                )
                        )
                )
        );
    }

    private static int getStatus(CommandSource source)
    {
        source.sendFeedback(new StringTextComponent(OpLockConfig.GENERAL.commandStatus.get() + String.valueOf(Oplock.INSTANCE.serverLockStatus).toUpperCase()), true);
        return 1;
    }

    private static int turnOnOpLock(CommandSource source, boolean shouldKick, ITextComponent message, ITextComponent customMessage) throws CommandSyntaxException
    {
        if (Oplock.INSTANCE.serverLockStatus)
            throw ALREADY_ON.create();
        else
        {
            Oplock.INSTANCE.serverLockStatus = true;
            if (shouldKick)
                kickInvalidPlayers(source, message);
            getStatus(source);
            notifyPlayers(new StringTextComponent(OpLockConfig.GENERAL.chatOnNotificationMessage.get()).func_230530_a_(Style.field_240709_b_.func_240713_a_(true).func_240718_a_(Color.func_240744_a_(TextFormatting.RED))), customMessage);
            return 1;
        }
    }

    private static int turnOffOpLock(CommandSource source)
    {
        Oplock.INSTANCE.serverLockStatus = false;
        getStatus(source);
        notifyPlayers(new StringTextComponent(OpLockConfig.GENERAL.chatOffNotificationMessage.get()).func_230530_a_(Style.field_240709_b_.func_240713_a_(true).func_240718_a_(Color.func_240744_a_(TextFormatting.GREEN))), null);
        return 1;
    }

    private static int kickInvalidPlayers(CommandSource source, ITextComponent message)
    {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        int playersKicked = 0;

        for (ServerPlayerEntity player : server.getPlayerList().getPlayers())
        {
            OpEntry target = server.getPlayerList().getOppedPlayers().getEntry(player.getGameProfile());

            if (target == null || target.getPermissionLevel() < OpLockConfig.GENERAL.allowedPermissionLevel.get())
            {
                if (message != null)
                {
                    player.connection.disconnect(message);
                }
                else
                {
                    player.connection.disconnect(new StringTextComponent(OpLockConfig.GENERAL.kickDisconnectMessage.get()).func_230530_a_(Style.field_240709_b_.func_240713_a_(true).func_240718_a_(Color.func_240744_a_(TextFormatting.RED))));
                }
                playersKicked++;
            }
        }

        getStatus(source);
        source.sendFeedback(new StringTextComponent(OpLockConfig.GENERAL.kickedPlayerCountMessage.get()).func_240702_b_(String.valueOf(playersKicked)), true);
        return 1;
    }

    private static void notifyPlayers(ITextComponent message, ITextComponent customMessage)
    {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        for (ServerPlayerEntity player : server.getPlayerList().getPlayers())
        {
            OpEntry target = server.getPlayerList().getOppedPlayers().getEntry(player.getGameProfile());

            if (target == null || target.getPermissionLevel() < OpLockConfig.GENERAL.allowedPermissionLevel.get())
                player.sendMessage(message, player.getUniqueID());

            if (customMessage != null)
            {
                player.sendMessage(new StringTextComponent("<OpLock> ").func_230530_a_(Style.field_240709_b_.func_240713_a_(true).func_240718_a_(Color.func_240744_a_(TextFormatting.AQUA))).func_230529_a_(customMessage), player.getUniqueID());
            }
        }
    }
}
