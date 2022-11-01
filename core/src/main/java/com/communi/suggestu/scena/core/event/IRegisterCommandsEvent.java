package com.communi.suggestu.scena.core.event;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

/**
 * Event fired when a command dispatcher is being build and custom commands are being collected.
 */
@FunctionalInterface
public interface IRegisterCommandsEvent extends IEvent {

    /**
     * Invoked when commands are going to be registered.
     *
     * @param dispatcher The dispatcher to register commands to;
     * @param commandBuildContext The context for the commands to operate and build in.
     */
    void handle(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext);
}
