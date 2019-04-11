package ru.dbotthepony.mc.gtcefe.mj;

import buildcraft.api.mj.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraftforge.energy.*;

public class EnergyWrapperMJ implements IMjConnector, IMjPassiveProvider, IMjReadable, IMjReceiver
{
    protected final TileEntity upvalue;
    protected IEnergyStorage container;
    EnumFacing face;
    
    public EnergyWrapperMJ(final TileEntity upvalue) {
        this.face = EnumFacing.SOUTH;
        this.upvalue = upvalue;
    }
    
    EnergyWrapperMJ face(final EnumFacing face) {
        this.face = face;
        return this;
    }
    
    private boolean updateCapability(final IEnergyStorage storage) {
        this.container = storage;
        return storage != null;
    }
    
    boolean isValid() {
        return this.upvalue.hasCapability(CapabilityEnergy.ENERGY, this.face) && this.updateCapability((IEnergyStorage)this.upvalue.getCapability(CapabilityEnergy.ENERGY, this.face));
    }
    
    public long getPowerRequested() {
        if (this.container == null) {
            return 0L;
        }
        return EnergyProviderMJ.fromRF(this.container.receiveEnergy(Integer.MAX_VALUE, true));
    }
    
    public long receivePower(final long microJoules, final boolean simulate) {
        if (this.container == null) {
            return 0L;
        }
        final int rf = EnergyProviderMJ.toRF(microJoules);
        if (rf == 0) {
            return 0L;
        }
        final int simulated = this.container.receiveEnergy(rf, true);
        if (simulated == 0) {
            return microJoules;
        }
        if (!simulate) {
            this.container.receiveEnergy(simulated, false);
        }
        return microJoules - EnergyProviderMJ.fromRF(simulated);
    }
    
    public long getStored() {
        if (this.container == null) {
            return 0L;
        }
        return EnergyProviderMJ.fromRF(this.container.getEnergyStored());
    }
    
    public long getCapacity() {
        if (this.container == null) {
            return 0L;
        }
        return EnergyProviderMJ.fromRF(this.container.getMaxEnergyStored());
    }
    
    public long extractPower(final long min, final long max, final boolean simulate) {
        if (this.container == null) {
            return 0L;
        }
        if (!this.container.canExtract()) {
            return 0L;
        }
        final int rfMax = EnergyProviderMJ.toRF(max);
        final int simulated = this.container.extractEnergy(rfMax, true);
        if (rfMax == simulated) {
            if (!simulate) {
                this.container.extractEnergy(rfMax, false);
            }
            return max;
        }
        final int rfMin = EnergyProviderMJ.toRF(min);
        if (rfMin > simulated) {
            return 0L;
        }
        if (!simulate) {
            this.container.extractEnergy(simulated, false);
        }
        return EnergyProviderMJ.fromRF(simulated);
    }
    
    public boolean canConnect(final IMjConnector other) {
        return this.container != null;
    }
}
