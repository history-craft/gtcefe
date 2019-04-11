package ru.dbotthepony.mc.gtcefe.eu;

import net.minecraft.tileentity.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraft.util.*;
import net.minecraftforge.energy.*;
import gregtech.api.capability.*;

public class EnergyProvider implements ICapabilityProvider
{
    private final TileEntity upvalue;
    private final EnergyContainerWrapper[] facesRF;
    private GregicEnergyContainerWrapper wrapper;
    private boolean gettingValue;
    
    public EnergyProvider(final TileEntity entCap) {
        this.facesRF = new EnergyContainerWrapper[7];
        this.gettingValue = false;
        this.upvalue = entCap;
    }
    
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing) {
        if (this.gettingValue) {
            return false;
        }
        if (capability != CapabilityEnergy.ENERGY && capability != GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER) {
            return false;
        }
        if (capability == CapabilityEnergy.ENERGY) {
            final int faceID = (facing == null) ? 6 : facing.getIndex();
            if (this.facesRF[faceID] == null) {
                this.facesRF[faceID] = new EnergyContainerWrapper(this.upvalue.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, facing), facing);
            }
            this.gettingValue = true;
            final boolean result = this.facesRF[faceID].isValid();
            this.gettingValue = false;
            return result;
        }
        if (this.wrapper == null) {
            this.wrapper = new GregicEnergyContainerWrapper(this.upvalue);
        }
        this.gettingValue = true;
        final boolean result2 = this.wrapper.isValid(facing);
        this.gettingValue = false;
        return result2;
    }
    
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing) {
        if (this.gettingValue) {
            return null;
        }
        if (!this.hasCapability(capability, facing)) {
            return null;
        }
        if (capability == CapabilityEnergy.ENERGY) {
            final int faceID = (facing == null) ? 6 : facing.getIndex();
            if (this.facesRF[faceID] == null) {
                this.facesRF[faceID] = new EnergyContainerWrapper(this.upvalue.getCapability(GregtechCapabilities.CAPABILITY_ENERGY_CONTAINER, facing), facing);
            }
            this.gettingValue = true;
            if (this.facesRF[faceID].isValid()) {
                this.gettingValue = false;
                return (T)this.facesRF[faceID];
            }
            this.gettingValue = false;
            return null;
        }
        else {
            if (this.wrapper == null) {
                this.wrapper = new GregicEnergyContainerWrapper(this.upvalue);
            }
            this.gettingValue = true;
            if (this.wrapper.isValid(facing)) {
                this.gettingValue = false;
                return (T)this.wrapper;
            }
            this.gettingValue = false;
            return null;
        }
    }
}
