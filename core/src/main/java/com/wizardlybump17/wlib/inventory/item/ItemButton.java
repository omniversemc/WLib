package com.wizardlybump17.wlib.inventory.item;

import lombok.Data;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Data
public class ItemButton {

    public static final ItemButton AIR = new ItemButton(new ItemStack(Material.AIR));

    private ItemStack itemStack;
    private final ClickAction clickAction;
    private final ClickAction0 clickAction0;
    private final Map<Integer, ItemButton> children = new HashMap<>();

    public ItemButton(ItemStack item) {
        this(item, (ClickAction) null);
    }

    public ItemButton(ItemStack item, ClickAction clickAction) {
        itemStack = item;
        this.clickAction = clickAction;
        clickAction0 = null;
    }

    public ItemButton(ItemStack item, ClickAction0 clickAction) {
        itemStack = item;
        this.clickAction = null;
        clickAction0 = clickAction;
    }

    public void addChild(int slot, ItemButton item) {
        children.put(slot, item);
    }

    public void removeChild(int slot) {
        children.remove(slot);
    }

    public interface ClickAction {
        void onClick(InventoryClickEvent event);
    }

    public interface ClickAction0 {
        void onClick(InventoryClickEvent event, int page);
    }
}
