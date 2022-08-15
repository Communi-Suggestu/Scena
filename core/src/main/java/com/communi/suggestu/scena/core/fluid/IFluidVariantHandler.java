package com.communi.suggestu.scena.core.fluid;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface IFluidVariantHandler
{
    /**
     * Return the name that should be used for the passed fluid variant.
     */
    Component getName(FluidInformation fluidInformation);

    /**
     * Return the sound corresponding to this fluid being filled, or none if no sound is available.
     *
     * <p>If a non-empty sound event is returned, {@link Fluid#getPickupSound} will return that sound.
     */
    Optional<SoundEvent> getFillSound(FluidInformation variant);

    /**
     * Return the sound corresponding to this fluid being emptied, or none if no sound is available.
     */
    Optional<SoundEvent> getEmptySound(FluidInformation variant);

    /**
     * Return an integer in [0, 15]: the light level emitted by this fluid, or 0 if it doesn't naturally emit light.
     */
    int getLuminance(FluidInformation variant);

    /**
     * Return a non-negative integer, representing the temperature of this fluid in Kelvin.
     */
    int getTemperature(FluidInformation variant);

    /**
     * Return a positive integer, representing the viscosity of this fluid.
     * Fluids with lower viscosity generally flow faster than fluids with higher viscosity.
     */
    int getViscosity(FluidInformation variant);

    /**
     * Returns the density of this fluid.
     * If the fluid is lighter than air, then it's density is less or equal to 0
     *
     * @param variant The fluid variant
     * @return The density of the fluid
     */
    int getDensity(FluidInformation variant);

    /**
     * Returns the color of the fluid.
     *
     * @param variant The fluid variant
     * @return The color of the fluid
     */
    int getTintColor(FluidInformation variant);

    /**
     * Returns the resource location of the still texture of the fluid.
     *
     * @param variant The fluid variant
     * @return The resource location of the still texture of the fluid
     */
    Optional<ResourceLocation> getStillTexture(FluidInformation variant);

    /**
     * Returns the resource location of the flowing texture of the fluid.
     *
     * @param variant The fluid variant
     * @return The resource location of the flowing texture of the fluid
     */
    Optional<ResourceLocation> getFlowingTexture(FluidInformation variant);
}
