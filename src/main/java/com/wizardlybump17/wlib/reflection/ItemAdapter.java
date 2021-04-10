package com.wizardlybump17.wlib.reflection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ItemAdapter {

    protected ItemStack target;
    protected final ReflectionAdapter mainAdapter;

    public abstract void setNbtTag(String key, Object value);
    public abstract void setNbtTags(Map<String, Object> tags);
    public abstract void removeNbtTag(String key);
    public abstract boolean hasNbtTag(String key);
    public abstract Object getNbtTag(String key);
    public abstract Map<String, Object> getNbtTags();
    public abstract Object getMainTag();
    public abstract void setMainTag(Object tag);
    public abstract Object toNmsStack();
}
