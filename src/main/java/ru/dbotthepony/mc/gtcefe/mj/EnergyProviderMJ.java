package ru.dbotthepony.mc.gtcefe.mj;

import net.minecraft.tileentity.*;
import ru.dbotthepony.mc.gtcefe.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraft.util.*;
import net.minecraftforge.energy.*;
import buildcraft.api.mj.*;

public class EnergyProviderMJ implements ICapabilityProvider
{
    private final TileEntity upvalue;
    private EnergyContainerMJ container;
    private EnergyWrapperMJ containerMJ;
    private boolean ignore;
    
    public EnergyProviderMJ(final TileEntity upvalue) {
        this.ignore = false;
        this.upvalue = upvalue;
    }
    
    private static int antiOverflow(final long value) {
        if (value > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int)value;
    }
    
    public static int toRF(long microJoules) {
        final long ratio = BCFE.conversionRatio();
        if (microJoules < ratio) {
            return 0;
        }
        microJoules -= microJoules % ratio;
        microJoules /= ratio;
        return antiOverflow(microJoules);
    }
    
    public static long fromRF(final int rf) {
        return rf * BCFE.conversionRatio();
    }
    
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing) {
        if (this.ignore) {
            return false;
        }
        if (capability == CapabilityEnergy.ENERGY) {
            if (this.container == null) {
                this.container = new EnergyContainerMJ(this.upvalue);
            }
            this.ignore = true;
            final boolean result = this.container.face(facing).isValid();
            this.ignore = false;
            return result;
        }
        if (capability == MjAPI.CAP_PASSIVE_PROVIDER || capability == MjAPI.CAP_RECEIVER || capability == MjAPI.CAP_READABLE || capability == MjAPI.CAP_CONNECTOR) {
            if (this.containerMJ == null) {
                this.containerMJ = new EnergyWrapperMJ(this.upvalue);
            }
            this.ignore = true;
            final boolean result = this.containerMJ.face(facing).isValid();
            this.ignore = false;
            return result;
        }
        return false;
    }
    
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing) {
        if (this.ignore) {
            return null;
        }
        if (capability == CapabilityEnergy.ENERGY) {
            if (this.container == null) {
                this.container = new EnergyContainerMJ(this.upvalue);
            }
            this.ignore = true;
            final EnergyContainerMJ container = this.container.face(facing).isValid() ? this.container.updateValues() : null;
            this.ignore = false;
            return (T)container;
        }
        if (capability == MjAPI.CAP_PASSIVE_PROVIDER || capability == MjAPI.CAP_RECEIVER || capability == MjAPI.CAP_READABLE || capability == MjAPI.CAP_CONNECTOR) {
            if (this.containerMJ == null) {
                this.containerMJ = new EnergyWrapperMJ(this.upvalue);
            }
            this.ignore = true;
            final EnergyWrapperMJ container2 = this.containerMJ.face(facing).isValid() ? this.containerMJ : null;
            this.ignore = false;
            return (T)container2;
        }
        return null;
    }
}
