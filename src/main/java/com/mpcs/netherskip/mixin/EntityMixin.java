package com.mpcs.netherskip.mixin;

import com.mpcs.netherskip.NetherSkipMod;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(at = @At("HEAD"),method = "tickPortal()V")
    void tickPortal(CallbackInfo callbackInfo) {
        EntityAccessors accessors = (EntityAccessors)this;
        if (NetherSkipMod.autoTeleportDisabled && accessors.getNetherPortalTime() != 999999) {
            ((EntityAccessors)this).setNetherPortalTime(0);
        }
    }
}
