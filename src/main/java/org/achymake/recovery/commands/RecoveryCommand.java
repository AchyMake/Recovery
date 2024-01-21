package org.achymake.recovery.commands;

import org.achymake.recovery.Recovery;
import org.achymake.recovery.files.Message;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RecoveryCommand implements CommandExecutor, TabCompleter {
    private Recovery getPlugin() {
        return Recovery.getInstance();
    }
    private Message getMessage() {
        return getPlugin().getMessage();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                getMessage().send(player, "&6" + getPlugin().getName() + " " + getPlugin().getDescription().getVersion());
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    getPlugin().reload();
                    getMessage().send(player, "&6Recovery:&f reloaded");
                }
            }
        }
        if (sender instanceof ConsoleCommandSender consoleCommandSender) {
            if (args.length == 0) {
                getMessage().send(consoleCommandSender, getPlugin().getName() + " " + getPlugin().getDescription().getVersion());
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    getPlugin().reload();
                    getMessage().send(consoleCommandSender, "Recovery: reloaded");
                }
            }
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player) {
            if (args.length == 1) {
                commands.add("reload");
            }
        }
        return commands;
    }
}