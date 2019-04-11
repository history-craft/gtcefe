package ru.dbotthepony.mc.gtcefe.mj;

import net.minecraftforge.energy.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import buildcraft.api.mj.*;
import ru.dbotthepony.mc.gtcefe.*;

class EnergyContainerMJ implements IEnergyStorage
{
    protected final TileEntity upvalue;
    EnumFacing face;
    private EnumFacing lastFace;
    protected IMjConnector connector;
    protected IMjReadable read;
    protected IMjReceiver receiver;
    protected IMjPassiveProvider passive;
    
    public EnergyContainerMJ(final TileEntity upvalue) {
        this.face = EnumFacing.SOUTH;
        this.upvalue = upvalue;
    }
    
    EnergyContainerMJ face(final EnumFacing face) {
        this.face = face;
        return this;
    }
    
    boolean isValid() {
        return this.upvalue.hasCapability(MjAPI.CAP_RECEIVER, this.face) || this.upvalue.hasCapability(MjAPI.CAP_CONNECTOR, this.face);
    }
    
    EnergyContainerMJ updateValues() {
        if (this.lastFace == this.face) {
            return this;
        }
        this.lastFace = this.face;
        this.connector = this.upvalue.getCapability(MjAPI.CAP_CONNECTOR, this.face);
        this.read = this.upvalue.getCapability(MjAPI.CAP_READABLE, this.face);
        this.receiver = this.upvalue.getCapability(MjAPI.CAP_RECEIVER, this.face);
        this.passive = this.upvalue.getCapability(MjAPI.CAP_PASSIVE_PROVIDER, this.face);
        return this;
    }
    
    public int receiveEnergy(int maxReceive, final boolean simulate) {
        if (!this.canReceive()) {
            return 0;
        }
        maxReceive = Math.min(this.calcMaxReceive(), maxReceive);
        if (maxReceive == 0) {
            return 0;
        }
        long value = EnergyProviderMJ.fromRF(maxReceive);
        long simulated = this.receiver.receivePower(value, true);
        if (simulated == 0L) {
            if (!simulate) {
                this.receiver.receivePower(value, false);
            }
            return maxReceive;
        }
        value -= simulated;
        final long ratio = BCFE.conversionRatio();
        if (value % ratio != 0L) {
            value -= value % ratio;
        }
        simulated = this.receiver.receivePower(value, true);
        if (simulated % ratio != 0L) {
            return 0;
        }
        if (!simulate) {
            return EnergyProviderMJ.toRF(value - this.receiver.receivePower(value, false));
        }
        return EnergyProviderMJ.toRF(value - simulated);
    }
    
    public int extractEnergy(final int maxExtract, final boolean simulate) {
        if (!this.canExtract()) {
            return 0;
        }
        final long value = EnergyProviderMJ.fromRF(maxExtract);
        long simulated = this.passive.extractPower(BCFE.conversionRatio(), value, true);
        simulated -= simulated % BCFE.conversionRatio();
        if (simulated == 0L) {
            return 0;
        }
        if (!simulate) {
            return EnergyProviderMJ.toRF(this.passive.extractPower(BCFE.conversionRatio(), simulated, true));
        }
        return EnergyProviderMJ.toRF(simulated);
    }
    
    int calcMaxReceive() {
        if (this.read == null) {
            return Integer.MAX_VALUE;
        }
        return this.getMaxEnergyStored() - this.getEnergyStored();
    }
    
    public int getEnergyStored() {
        return (this.read != null) ? EnergyProviderMJ.toRF(this.read.getStored()) : 0;
    }
    
    public int getMaxEnergyStored() {
        return (this.read != null) ? EnergyProviderMJ.toRF(this.read.getCapacity()) : 0;
    }
    
    public boolean canExtract() {
        return this.passive != null;
    }
    
    public boolean canReceive() {
        return this.receiver != null && this.receiver.canReceive();
    }
}
