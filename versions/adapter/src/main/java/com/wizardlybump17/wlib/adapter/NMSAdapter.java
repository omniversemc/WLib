package com.wizardlybump17.wlib.adapter;

import lombok.Data;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

@Data
public abstract class NMSAdapter {

    public abstract Object nbtToJava(Object nbt);
    public abstract Object javaToNbt(Object java);
    public abstract String getTargetVersion();

    public abstract ItemAdapter getItemAdapter(ItemStack item);
    public EntityAdapter getEntityAdapter(Entity entity) {
        if (EntityAdapter.ENTITY_CACHE.containsKey(entity.getUniqueId()))
            return EntityAdapter.ENTITY_CACHE.get(entity.getUniqueId());
        EntityAdapter adapter = newEntityAdapter(entity);
        EntityAdapter.ENTITY_CACHE.put(entity.getUniqueId(), adapter);
        return adapter;
    }
    public abstract EntityAdapter newEntityAdapter(Entity entity);

    public abstract ItemStack getFixedMaterial(WMaterial material);
    public final String getGlowTag() {
        return "WLib-glowing";
    }
}
