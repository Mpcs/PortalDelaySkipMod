package com.mpcs.netherskip.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessors  {

    @Accessor
    boolean getInNetherPortal();

    @Accessor("netherPortalTime")
    void setNetherPortalTime(int netherPortalTime);

    @Accessor()
    int getNetherPortalTime();

}
