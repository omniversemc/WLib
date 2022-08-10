package com.wizardlybump17.wlib.command;

import com.wizardlybump17.wlib.command.sender.ConsoleSender;
import com.wizardlybump17.wlib.command.sender.GenericSender;
import com.wizardlybump17.wlib.command.sender.PlayerSender;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.command.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class BukkitCommandExecutor implements CommandExecutor, com.wizardlybump17.wlib.command.holder.CommandExecutor {

    private final CommandManager manager;

    @SneakyThrows
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        com.wizardlybump17.wlib.command.CommandSender<?> commandSender;
        if (sender instanceof Player)
            commandSender = new PlayerSender(((Player) sender));
        else if (sender instanceof ConsoleCommandSender)
            commandSender = new ConsoleSender(((ConsoleCommandSender) sender));
        else if (sender instanceof BlockCommandSender)
            commandSender = new com.wizardlybump17.wlib.command.sender.BlockCommandSender((BlockCommandSender) sender);
        else
            commandSender = new GenericSender(sender);
        execute(commandSender, command.getName(), args);
        return false;
    }

    @Override
    public void execute(com.wizardlybump17.wlib.command.CommandSender<?> sender, String commandName, String[] args) {
        String commandExecution = commandName + " " + String.join(" ", args);
        manager.execute(sender, commandExecution);
    }
}
