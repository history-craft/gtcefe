package ru.dbotthepony.mc.gtcefe.eu;

import net.minecraft.item.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraft.util.*;
import net.minecraftforge.energy.*;
import gregtech.api.capability.*;

public class EnergyProviderItem implements ICapabilityProvider
{
    private final ItemStack upvalue;
    private ItemEnergyContainerWrapper rfwrapper;
    private GregicEnergyContainerWrapper wrapper;
    private final boolean valid;
    
    public EnergyProviderItem(final ItemStack entCap) {
        this.upvalue = entCap;
        this.valid = (entCap.getCount() == 1);
    }
    
    public boolean hasCapability(final Capability<?> capability, final EnumFacing facing) {
        if (!this.valid) {
            return false;
        }
        if (capability == CapabilityEnergy.ENERGY) {
            if (this.rfwrapper == null) {
                this.rfwrapper = new ItemEnergyContainerWrapper(this.upvalue.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null));
            }
            return this.rfwrapper.isValid();
        }
        return false;
    }
    
    public <T> T getCapability(final Capability<T> capability, final EnumFacing facing) {
        if (!this.hasCapability(capability, facing)) {
            return null;
        }
        if (capability != CapabilityEnergy.ENERGY) {
            return null;
        }
        if (this.rfwrapper == null) {
            this.rfwrapper = new ItemEnergyContainerWrapper(this.upvalue.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null));
        }
        if (this.rfwrapper.isValid()) {
            return (T)this.rfwrapper;
        }
        return null;
    }
}
