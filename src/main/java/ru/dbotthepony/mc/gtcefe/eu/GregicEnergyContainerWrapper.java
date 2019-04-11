package ru.dbotthepony.mc.gtcefe.eu;

import gregtech.api.capability.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraft.util.*;
import net.minecraftforge.energy.*;
import javax.annotation.*;
import gregtech.api.*;

public class GregicEnergyContainerWrapper implements IEnergyContainer
{
    private final ICapabilityProvider upvalue;
    private final IEnergyStorage[] facesRF;
    
    public GregicEnergyContainerWrapper(final ICapabilityProvider upvalue) {
        this.facesRF = new IEnergyStorage[7];
        this.upvalue = upvalue;
    }
    
    boolean isValid(final EnumFacing face) {
        if (this.upvalue.hasCapability(CapabilityEnergy.ENERGY, face)) {
            return true;
        }
        if (face == null) {
            for (final EnumFacing face2 : EnumFacing.VALUES) {
                if (this.upvalue.hasCapability(CapabilityEnergy.ENERGY, face2)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private IEnergyStorage getStorageCap() {
        IEnergyStorage container = this.def();
        if (container != null && container.getMaxEnergyStored() > 0) {
            return container;
        }
        for (final EnumFacing face : EnumFacing.VALUES) {
            container = this.facesRF[face.getIndex()];
            if (container == null) {
                container = this.upvalue.getCapability(CapabilityEnergy.ENERGY, face);
                this.facesRF[face.getIndex()] = container;
            }
            if (container != null && container.getMaxEnergyStored() > 0) {
                return container;
            }
        }
        return container;
    }
    
    private IEnergyStorage getAcceptionCap() {
        IEnergyStorage container = this.def();
        if (container != null && container.receiveEnergy(Integer.MAX_VALUE, true) > 0) {
            return container;
        }
        for (final EnumFacing face : EnumFacing.VALUES) {
            container = this.facesRF[face.getIndex()];
            if (container == null) {
                container = this.upvalue.getCapability(CapabilityEnergy.ENERGY, face);
                this.facesRF[face.getIndex()] = container;
            }
            if (container != null && container.receiveEnergy(Integer.MAX_VALUE, true) > 0) {
                return container;
            }
        }
        return container;
    }
    
    public long acceptEnergyFromNetwork(final EnumFacing facing, final long voltage, final long amperage) {
        final int faceID = (facing == null) ? 6 : facing.getIndex();
        IEnergyStorage container = this.facesRF[faceID];
        if (container == null) {
            container = this.upvalue.getCapability(CapabilityEnergy.ENERGY, facing);
            this.facesRF[faceID] = container;
        }
        if (container == null) {
            return 0L;
        }
        long maximalValue = voltage * amperage * 4L;
        if (maximalValue > 2147483647L) {
            maximalValue = 2147483647L;
        }
        int receive = container.receiveEnergy((int)maximalValue, true);
        receive -= (int)(receive % (voltage * 4L));
        if (receive == 0) {
            return 0L;
        }
        return container.receiveEnergy(receive, false) / (voltage * 4L);
    }
    
    public long changeEnergy(final long delta) {
        final IEnergyStorage container = this.getStorageCap();
        if (container == null) {
            return 0L;
        }
        if (delta == 0L) {
            return 0L;
        }
        if (delta < 0L) {
            long extractValue = delta * 4L;
            if (extractValue > 2147483647L) {
                extractValue = 2147483647L;
            }
            int extract = container.extractEnergy((int)extractValue, true);
            extract -= extract % 4;
            return container.extractEnergy(extract, false) / 4L;
        }
        long receiveValue = delta * 4L;
        if (receiveValue > 2147483647L) {
            receiveValue = 2147483647L;
        }
        int receive = container.receiveEnergy((int)receiveValue, true);
        receive -= receive % 4;
        return container.receiveEnergy(receive, false) / 4L;
    }
    
    @Nullable
    private IEnergyStorage def() {
        if (this.facesRF[6] == null) {
            this.facesRF[6] = this.upvalue.getCapability(CapabilityEnergy.ENERGY, null);
        }
        return this.facesRF[6];
    }
    
    public long getEnergyCapacity() {
        final IEnergyStorage cap = this.getStorageCap();
        if (cap == null) {
            return 0L;
        }
        int value = cap.getMaxEnergyStored();
        value -= value % 4;
        return value / 4;
    }
    
    public long getEnergyStored() {
        final IEnergyStorage cap = this.getStorageCap();
        if (cap == null) {
            return 0L;
        }
        int value = cap.getEnergyStored();
        value -= value % 4;
        return value / 4;
    }
    
    public long getInputAmperage() {
        final IEnergyStorage container = this.getAcceptionCap();
        if (container == null) {
            return 0L;
        }
        final long voltage = this.getInputVoltage();
        if (voltage == GTValues.V[GTValues.V.length]) {
            return 1L;
        }
        int index = 0;
        while (index < GTValues.V.length) {
            if (GTValues.V[index] == voltage) {
                long voltageNext = GTValues.V[index + 1] * 4L;
                if (voltageNext > 2147483647L) {
                    voltageNext = 2147483647L;
                }
                int allowedInput = container.receiveEnergy((int)voltageNext, true);
                if (allowedInput < voltage * 4L) {
                    return 1L;
                }
                allowedInput -= (int)(allowedInput % voltage * 4L);
                return allowedInput / (voltage * 4L);
            }
            else {
                ++index;
            }
        }
        return 1L;
    }
    
    public long getInputVoltage() {
        final IEnergyStorage container = this.getStorageCap();
        if (container == null) {
            return 0L;
        }
        long grabMaxInput = container.receiveEnergy(Integer.MAX_VALUE, true);
        grabMaxInput -= grabMaxInput % 4L;
        if (grabMaxInput == 0L) {
            return 0L;
        }
        grabMaxInput /= 4L;
        long value = GTValues.V[0];
        if (grabMaxInput < value) {
            return 0L;
        }
        for (final long value2 : GTValues.V) {
            if (value2 < grabMaxInput) {
                break;
            }
            value = value2;
        }
        return value;
    }
    
    public boolean inputsEnergy(final EnumFacing facing) {
        final int faceID = (facing == null) ? 6 : facing.getIndex();
        IEnergyStorage container = this.facesRF[faceID];
        if (container == null) {
            container = this.upvalue.getCapability(CapabilityEnergy.ENERGY, facing);
            this.facesRF[faceID] = container;
        }
        return container != null && container.canReceive();
    }
    
    public boolean outputsEnergy(final EnumFacing arg0) {
        return false;
    }
}
