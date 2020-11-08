package com.wizardlybump17.wlib.command;

import com.wizardlybump17.wlib.inventory.item.ItemButton;
import com.wizardlybump17.wlib.inventory.paginated.PaginatedInventoryBuilder;
import com.wizardlybump17.wlib.item.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestCommand extends WCommand {

    public TestCommand(JavaPlugin plugin) {
        super(plugin, "test");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) return true;
        Player player = (Player) sender;
        switch (args[0]) {
            case "0": {
                test0(player);
                return true;
            }

            case "1": {
                test1(player);
                return true;
            }

            case "2": {
                test2(player);
                return true;
            }

            case "3": {
                test3(player);
                return true;
            }

            case "4": {
                test4(player);
                return true;
            }
        }
        return true;
    }

    private void test0(Player player) {
        PaginatedInventoryBuilder paginatedInventory = new PaginatedInventoryBuilder("§cTest 0", 9 * 6,
                "#########" +
                        "#xxxxxxx#" +
                        "#xxxxxxx#" +
                        "#xxxxxxx#" +
                        "#xxxxxxx#" +
                        "<#######>"
        );

        List<ItemButton> itemButtons = new ArrayList<>();
        Random random = new Random();
        Material[] materials = new Material[]{Material.ACACIA_DOOR_ITEM, Material.BEDROCK, Material.COBBLESTONE};
        for (int i = 0; i < 300; i++) {
            Material material = materials[random.nextInt(materials.length)];
            itemButtons.add(new ItemButton(new ItemStack(material), event -> event.getWhoClicked().sendMessage("§aYou clicked in " + material)));
        }

        paginatedInventory
                .nextPageItemStack(
                        new ItemBuilder(Material.ARROW)
                                .displayName("§aNext page")
                                .build())
                .previousPageItemStack(
                        new ItemBuilder(Material.ARROW)
                                .displayName("§aPrevious page")
                                .build())
                .border(new ItemButton(
                        new ItemBuilder(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getData())
                                .displayName(" ")
                                .build()
                ))
                .items(itemButtons);

        paginatedInventory.build().openPage(player, 0);
    }

    private void test1(Player player) {
        PaginatedInventoryBuilder paginatedInventory = new PaginatedInventoryBuilder("§aTest 1", 9 * 6,
                "xxxxxxxxx" +
                        "xxxxxxxxx" +
                        "xxxxxxxxx" +
                        "xxxxxxxxx" +
                        "xxxxxxxxx" +
                        "<xxxxxxx>"
        );

        List<ItemButton> itemButtons = new ArrayList<>();
        Random random = new Random();
        Material[] materials = new Material[]{Material.APPLE, Material.GOLD_BLOCK, Material.CAULDRON_ITEM};
        for (int i = 0; i < 500; i++) {
            Material material = materials[random.nextInt(materials.length)];
            itemButtons.add(new ItemButton(new ItemStack(material), event -> event.getWhoClicked().sendMessage("§aYou clicked in " + material)));
        }

        paginatedInventory
                .nextPageItemStack(
                        new ItemBuilder(Material.ARROW)
                                .displayName("§aNext page")
                                .build())
                .previousPageItemStack(
                        new ItemBuilder(Material.ARROW)
                                .displayName("§aPrevious page")
                                .build())
                .items(itemButtons);

        paginatedInventory.build().openPage(player, 0);
    }

    private void test2(Player player) {
        PaginatedInventoryBuilder paginatedInventory = new PaginatedInventoryBuilder("§bTest 2", 9 * 6,
                "xxxxxxxxx" +
                        "xxxx1xxxx" +
                        "xxx101xxx" +
                        "xx12021xx" +
                        "x2210122x" +
                        "<xxxxxxx>"
        );

        List<ItemButton> itemButtons = new ArrayList<>();
        Random random = new Random();
        Material[] materials = new Material[]{Material.CARROT_ITEM, Material.EMERALD_BLOCK, Material.STONE};
        for (int i = 0; i < 500; i++) {
            Material material = materials[random.nextInt(materials.length)];
            itemButtons.add(new ItemButton(new ItemStack(material), event -> event.getWhoClicked().sendMessage("§aYou clicked in " + material)));
        }

        paginatedInventory
                .nextPageItemStack(
                        new ItemBuilder(Material.ARROW)
                                .displayName("§aNext page")
                                .build())
                .previousPageItemStack(
                        new ItemBuilder(Material.ARROW)
                                .displayName("§aPrevious page")
                                .build())
                .addShapeReplacement('0', null)
                .addShapeReplacement(
                        '1',
                        new ItemButton(
                                new ItemBuilder(Material.ANVIL)
                                        .displayName("§7Anvil")
                                        .glow(true)
                                        .build(),
                                event -> event.getWhoClicked().damage(6)))
                .addShapeReplacement(
                        '2',
                        new ItemButton(
                                new ItemBuilder(Material.APPLE)
                                        .displayName("§cApple")
                                        .glow(true)
                                        .build(),
                                event -> event.getWhoClicked().setHealth(event.getWhoClicked().getHealth() + 6)))
                .items(itemButtons);

        paginatedInventory.build().openPage(player, 0);
    }

    private void test3(Player player) {
        PaginatedInventoryBuilder paginatedInventory = new PaginatedInventoryBuilder("§dTest 3", 9 * 6,
                "#########" +
                        "#xxx1xxx#" +
                        "#xx101xx#" +
                        "#x12021x#" +
                        "#2210122#" +
                        "<#######>"
        );

        List<ItemButton> itemButtons = new ArrayList<>();
        Random random = new Random();
        Material[] materials = new Material[]{Material.CARROT_ITEM, Material.EMERALD_BLOCK, Material.STONE};
        for (int i = 0; i < 500; i++) {
            Material material = materials[random.nextInt(materials.length)];
            itemButtons.add(new ItemButton(new ItemStack(material), event -> event.getWhoClicked().sendMessage("§aYou clicked in " + material)));
        }

        paginatedInventory
                .border(new ItemButton(
                        new ItemBuilder(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getData())
                                .displayName(" ")
                                .build()
                ))
                .nextPageItemStack(
                        new ItemBuilder(Material.ARROW)
                                .displayName("§aNext page")
                                .build())
                .previousPageItemStack(
                        new ItemBuilder(Material.ARROW)
                                .displayName("§aPrevious page")
                                .build())
                .addShapeReplacement('0', null)
                .addShapeReplacement(
                        '1',
                        new ItemButton(
                                new ItemBuilder(Material.ANVIL)
                                        .displayName("§7Anvil")
                                        .glow(true)
                                        .build(),
                                event -> event.getWhoClicked().damage(6)))
                .addShapeReplacement(
                        '2',
                        new ItemButton(
                                new ItemBuilder(Material.APPLE)
                                        .displayName("§cApple")
                                        .glow(true)
                                        .build(),
                                event -> event.getWhoClicked().setHealth(event.getWhoClicked().getHealth() + 6)))
                .items(itemButtons);

        paginatedInventory.build().openPage(player, 0);
    }

    private void test4(Player player) {
        PaginatedInventoryBuilder paginatedInventory = new PaginatedInventoryBuilder("§eTest 4", 9 * 6,
                "#xxxxxxx#" +
                        "#xxx1xxx#" +
                        "#xx101xx#" +
                        "#x12021x#" +
                        "#2210122#" +
                        "<xxxxxxx>"
        );

        List<ItemButton> itemButtons = new ArrayList<>();
        Random random = new Random();
        Material[] materials = new Material[]{Material.CACTUS, Material.CHEST, Material.SPONGE};
        for (int i = 0; i < 500; i++) {
            Material material = materials[random.nextInt(materials.length)];
            itemButtons.add(new ItemButton(new ItemStack(material), event -> event.getWhoClicked().sendMessage("§aYou clicked in " + material)));
        }

        paginatedInventory
                .border(new ItemButton(
                        new ItemBuilder(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getData())
                                .displayName(" ")
                                .build()
                ))
                .nextPageItemStack(
                        new ItemBuilder(Material.ARROW)
                                .displayName("§aNext page")
                                .build())
                .previousPageItemStack(
                        new ItemBuilder(Material.ARROW)
                                .displayName("§aPrevious page")
                                .build())
                .addShapeReplacement('0', null)
                .addShapeReplacement(
                        '1',
                        new ItemButton(
                                new ItemBuilder(Material.ANVIL)
                                        .displayName("§7Anvil")
                                        .glow(true)
                                        .build(),
                                event -> event.getWhoClicked().damage(6)))
                .addShapeReplacement(
                        '2',
                        new ItemButton(
                                new ItemBuilder(Material.APPLE)
                                        .displayName("§cApple")
                                        .glow(true)
                                        .build(),
                                event -> event.getWhoClicked().setHealth(event.getWhoClicked().getHealth() + 6)))
                .items(itemButtons);

        paginatedInventory.build().openPage(player, 0);
    }
}