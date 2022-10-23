package com.wizardlybump17.wlib.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

@UtilityClass
public class ItemUtil {

    public static String toBase64(ItemStack item) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack fromBase64(String base64) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toBase64(InventoryView view) {
        Inventory inventory = view.getTopInventory();
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeUTF(view.getTitle());
            dataOutput.writeByte(inventory.getSize());
            dataOutput.writeUTF(inventory.getType().name());

            for (ItemStack item : inventory)
                dataOutput.writeObject(item);

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Reads the given Base64 string and returns an inventory.<br>
     * No holder is used for the inventory
     *
     * @param base64 the Base64 string
     * @return the inventory
     */
    public static Inventory fromBase64Inventory(String base64) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            String title = dataInput.readUTF();
            int size = dataInput.readByte();

            InventoryType type = InventoryType.valueOf(dataInput.readUTF());
            Inventory inventory;
            if (type == InventoryType.CHEST)
                inventory = Bukkit.createInventory(null, size, title);
            else
                inventory = Bukkit.createInventory(null, type, title);

            for (int i = 0; i < size; i++)
                inventory.setItem(i, (ItemStack) dataInput.readObject());

            dataInput.close();
            return inventory;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean canFit(Inventory inventory, ItemStack... items) {
        Validate.noNullElements(items, "Item cannot be null");
        ItemStack[] inventoryItems = clone(inventory.getContents());
        items = clone(items);

        for (ItemStack item : items) {
            while (true) {
                int firstPartial = firstPartial(inventoryItems, item);
                if (firstPartial == -1) {
                    int firstFree = inventory.firstEmpty();
                    if (firstFree == -1)
                        return false;

                    if (item.getAmount() > inventory.getMaxStackSize()) {
                        ItemStack stack = item.clone();
                        stack.setAmount(inventory.getMaxStackSize());
                        setItem(inventoryItems, firstFree, stack);
                        item.setAmount(item.getAmount() - inventory.getMaxStackSize());
                    }

                    setItem(inventoryItems, firstFree, item);
                    break;
                }

                ItemStack partialItem = inventoryItems[firstPartial];
                int amount = item.getAmount();
                int partialAmount = partialItem.getAmount();
                int maxAmount = partialItem.getMaxStackSize();
                if (amount + partialAmount <= maxAmount) {
                    partialItem.setAmount(amount + partialAmount);
                    setItem(inventoryItems, firstPartial, partialItem);
                    break;
                }

                partialItem.setAmount(maxAmount);
                setItem(inventoryItems, firstPartial, partialItem);
                item.setAmount(amount + partialAmount - maxAmount);
            }
        }

        return true;
    }

    private static ItemStack[] clone(ItemStack... items) {
        ItemStack[] target = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++)
            target[i] = items[i] == null ? null : items[i].clone();
        return target;
    }

    private static int firstPartial(ItemStack[] items, ItemStack item) {
        for (int i = 0; i < items.length; i++) {
            ItemStack cItem = items[i];
            if (cItem != null && cItem.getAmount() < cItem.getMaxStackSize() && cItem.isSimilar(item))
                return i;
        }

        return -1;
    }

    private static void setItem(ItemStack[] items, int slot, ItemStack item) {
        items[slot] = item;
    }

    public static boolean canRemoveAll(Inventory inventory, ItemStack... items) {
        Validate.notNull(items, "Items cannot be null");
        HashMap<Integer, ItemStack> leftover = new HashMap<>();
        ItemStack[] content = clone(inventory.getContents());
        items = clone(items);

        for (int i = 0; i < items.length; ++i) {
            ItemStack item = items[i];
            int toDelete = item.getAmount();

            while (true) {
                int first = first(item, false, content);
                if (first == -1) {
                    item.setAmount(toDelete);
                    leftover.put(i, item);
                    break;
                }

                ItemStack itemStack = content[first];
                int amount = itemStack.getAmount();
                if (amount <= toDelete) {
                    toDelete -= amount;
                    content[first] = null;
                } else {
                    itemStack.setAmount(amount - toDelete);
                    setItem(content, first, itemStack);
                    toDelete = 0;
                }

                if (toDelete <= 0)
                    break;
            }
        }

        return leftover.isEmpty();
    }

    private static int first(ItemStack item, boolean withAmount, ItemStack[] inventory) {
        if (item == null)
            return -1;

        int i = 0;

        while (true) {
            if (i >= inventory.length)
                return -1;

            if (inventory[i] != null) {
                if (withAmount) {
                    if (item.equals(inventory[i])) {
                        break;
                    }
                } else if (item.isSimilar(inventory[i])) {
                    break;
                }
            }

            i++;
        }

        return i;
    }

    public static int getEnoughSpace(Inventory inventory, ItemStack item) {
        int space = 0;
        for (ItemStack i : inventory) {
            if (i == null)
                space += item.getMaxStackSize();
            else if (i.isSimilar(item))
                space += i.getMaxStackSize() - i.getAmount();
        }
        return space;
    }
}
