package ru.dbotthepony.mc.gtcefe.eu;

import net.minecraftforge.energy.*;
import gregtech.api.capability.*;
import gregtech.api.*;

public class ItemEnergyContainerWrapper implements IEnergyStorage
{
    private final IElectricItem container;
    
    public ItemEnergyContainerWrapper(final IElectricItem container) {
        this.container = container;
    }
    
    boolean isValid() {
        return this.container != null;
    }
    
    private long itemVoltage() {
        return GTValues.V[this.container.getTier()];
    }
    
    private int getMaxSpeed() {
        final long result = this.itemVoltage() * 4L;
        if (result > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int)result;
    }
    
    public int receiveEnergy(int maxReceive, final boolean simulate) {
        if (!this.canReceive()) {
            return 0;
        }
        if (this.container.getMaxCharge() <= 0L) {
            return 0;
        }
        final int speed = this.getMaxSpeed();
        if (maxReceive > speed) {
            maxReceive = speed;
        }
        maxReceive -= maxReceive % 4;
        if (maxReceive <= 0) {
            return 0;
        }
        final long simulated = this.container.charge(maxReceive / 4L, Integer.MAX_VALUE, false, true);
        if (simulated < 0L || simulated < this.itemVoltage()) {
            return 0;
        }
        this.container.charge(simulated, Integer.MAX_VALUE, false, false);
        return (int)(simulated * 4L);
    }
    
    public int extractEnergy(int maxExtract, final boolean simulate) {
        if (!this.canExtract()) {
            return 0;
        }
        if (this.container.getMaxCharge() <= 0L) {
            return 0;
        }
        final int speed = this.getMaxSpeed();
        if (maxExtract > speed) {
            maxExtract = speed;
        }
        maxExtract -= maxExtract % 4;
        if (maxExtract <= 0) {
            return 0;
        }
        final long simulated = this.container.discharge(maxExtract / 4L, Integer.MAX_VALUE, false, true, true);
        if (simulated < 0L) {
            return 0;
        }
        this.container.discharge(simulated, Integer.MAX_VALUE, false, true, false);
        return (int)(simulated * 4L);
    }
    
    public int getEnergyStored() {
        final long value = this.container.discharge(Long.MAX_VALUE, Integer.MAX_VALUE, true, false, true);
        if (value > 536870910L) {
            return Integer.MAX_VALUE;
        }
        return (int)(value * 4L);
    }
    
    public int getMaxEnergyStored() {
        final long discharge = this.container.discharge(Long.MAX_VALUE, Integer.MAX_VALUE, true, false, true);
        final long charge = this.container.charge(Long.MAX_VALUE, Integer.MAX_VALUE, true, true);
        final long diff = discharge + charge;
        if (diff > 536870910L) {
            return Integer.MAX_VALUE;
        }
        return (int)(diff * 4L);
    }
    
    public boolean canExtract() {
        return this.container.canProvideChargeExternally();
    }
    
    public boolean canReceive() {
        return this.container.charge(Long.MAX_VALUE, Integer.MAX_VALUE, true, true) != 0L;
    }
}
