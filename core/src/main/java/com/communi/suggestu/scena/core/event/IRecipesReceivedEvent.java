package com.communi.suggestu.scena.core.event;

import net.minecraft.world.item.crafting.RecipeMap;

/**
 * Event received on the client when recipes sync.
 */
public interface IRecipesReceivedEvent extends IEvent
{
    /**
     * Called by the event to deal with recipes.
     *
     * @param recipes Recipes.
     */
    void handle(RecipeMap recipes);
}
