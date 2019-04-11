package ru.dbotthepony.mc.gtcefe;

import net.minecraft.util.*;
import org.apache.logging.log4j.*;
import buildcraft.api.mj.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.common.*;
import net.minecraftforge.event.*;
import net.minecraft.tileentity.*;
import ru.dbotthepony.mc.gtcefe.mj.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.fml.common.eventhandler.*;

@Mod(modid = "bcfe", name = "BuildCraft FE Adapter", dependencies = "after:buildcraft;", version = "1.0", acceptedMinecraftVersions = "[1.12.2]")
public class BCFE
{
    public static final String MODID = "bcfe";
    public static final String NAME = "BuildCraft FE Adapter";
    public static final String VERSION = "1.0";
    public static final long RATIO = 40L;
    public static final int RATIO_INT = 40;
    public ResourceLocation resourceLocation;
    public static Logger logger;
    
    public static long conversionRatio() {
        return MjAPI.ONE_MINECRAFT_JOULE / 40L;
    }
    
    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        this.resourceLocation = new ResourceLocation("bcfe", "fecapability");
        BCFE.logger = event.getModLog();
    }
    
    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
        if (!Loader.isModLoaded("buildcraftcore")) {
            return;
        }
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void attachTileCapability(final AttachCapabilitiesEvent<TileEntity> event) {
        event.addCapability(this.resourceLocation, new EnergyProviderMJ(event.getObject()));
    }
}
