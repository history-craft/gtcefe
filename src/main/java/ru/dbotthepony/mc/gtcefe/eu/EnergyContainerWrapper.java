package ru.dbotthepony.mc.gtcefe.eu;

import net.minecraftforge.energy.*;
import gregtech.api.capability.*;
import net.minecraft.util.*;
import gregtech.common.pipelike.cable.tile.*;

public class EnergyContainerWrapper implements IEnergyStorage
{
    private final IEnergyContainer container;
    private EnumFacing facing;
    
    public EnergyContainerWrapper(final IEnergyContainer container, final EnumFacing facing) {
        this.facing = null;
        this.container = container;
        this.facing = facing;
    }
    
    boolean isValid() {
        return this.container != null && !(this.container instanceof GregicEnergyContainerWrapper);
    }
    
    private int maxSpeedIn() {
        final long result = this.container.getInputAmperage() * this.container.getInputVoltage() * 4L;
        if (result > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int)result;
    }
    
    private int maxSpeedOut() {
        final long result = this.container.getOutputAmperage() * this.container.getOutputVoltage() * 4L;
        if (result > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int)result;
    }
    
    private int voltageIn() {
        final long result = this.container.getInputVoltage() * 4L;
        if (result > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int)result;
    }
    
    private int voltageOut() {
        final long result = this.container.getOutputVoltage() * 4L;
        if (result > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int)result;
    }
    
    public int receiveEnergy(int maxReceive, final boolean simulate) {
        if (!this.canReceive()) {
            return 0;
        }
        final int speed = this.maxSpeedIn();
        if (maxReceive > speed) {
            maxReceive = speed;
        }
        maxReceive -= maxReceive % 4;
        maxReceive -= maxReceive % this.voltageIn();
        if (maxReceive <= 0 || maxReceive < this.voltageIn()) {
            return 0;
        }
        final long missing = this.container.getEnergyCanBeInserted() * 4L;
        if (missing <= 0L || missing < this.voltageIn()) {
            return 0;
        }
        if (missing < maxReceive) {
            maxReceive = (int)missing;
        }
        if (!simulate) {
            final int ampers = (int)this.container.acceptEnergyFromNetwork(this.facing, this.container.getInputVoltage(), maxReceive / (4L * this.container.getInputVoltage()));
            return ampers * this.voltageIn();
        }
        return maxReceive;
    }
    
    public int extractEnergy(int maxExtract, final boolean simulate) {
        if (!this.canExtract()) {
            return 0;
        }
        final int speed = this.maxSpeedOut();
        if (maxExtract > speed) {
            maxExtract = speed;
        }
        maxExtract -= maxExtract % 4;
        maxExtract -= maxExtract % this.voltageOut();
        if (maxExtract <= 0) {
            return 0;
        }
        final long stored = this.container.getEnergyStored() * 4L;
        if (stored <= 0L) {
            return 0;
        }
        if (stored < maxExtract) {
            maxExtract = (int)stored;
        }
        if (!simulate) {
            return (int)(this.container.removeEnergy(maxExtract / 4L) * 4L);
        }
        return maxExtract;
    }
    
    public int getEnergyStored() {
        final long stored = this.container.getEnergyStored() * 4L;
        if (stored > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int)stored;
    }
    
    public int getMaxEnergyStored() {
        final long maximal = this.container.getEnergyCapacity() * 4L;
        if (maximal > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int)maximal;
    }
    
    public boolean canExtract() {
        return !(this.container instanceof CableEnergyContainer) && this.container.outputsEnergy(this.facing);
    }
    
    public boolean canReceive() {
        return this.container.inputsEnergy(this.facing);
    }
}
