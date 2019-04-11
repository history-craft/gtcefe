package ru.dbotthepony.mc.gtcefe;

import net.minecraftforge.fml.common.*;
import net.minecraft.util.*;
import org.apache.logging.log4j.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.common.*;
import net.minecraftforge.event.*;
import net.minecraft.tileentity.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.item.*;
import ru.dbotthepony.mc.gtcefe.eu.*;

@Mod(modid = "gtcefe", name = "GregTechCE FE Adapter", dependencies = "required:gregtech;after:buildcraft;", version = "1.0", acceptedMinecraftVersions = "[1.12.2]")
public class GTCEFE
{
    public static final String MODID = "gtcefe";
    public static final String NAME = "GregTechCE FE Adapter";
    public static final String VERSION = "1.0";
    public static final long RATIO = 4L;
    public static final int RATIO_INT = 4;
    public ResourceLocation resourceLocation;
    public static Logger logger;
    
    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        this.resourceLocation = new ResourceLocation("gtcefe", "fecapability");
        GTCEFE.logger = event.getModLog();
    }
    
    @SubscribeEvent
    public void attachTileCapability(final AttachCapabilitiesEvent<TileEntity> event) {
        event.addCapability(this.resourceLocation, new EnergyProvider(event.getObject()));
    }
    
    @SubscribeEvent
    public void attachItemCapability(final AttachCapabilitiesEvent<ItemStack> event) {
        event.addCapability(this.resourceLocation, new EnergyProviderItem(event.getObject()));
    }
}
