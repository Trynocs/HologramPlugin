package com.trynocs.hologramPlugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.trynocs.hologramPlugin.main;
import com.trynocs.hologramPlugin.utils.config.Configmanager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.Arrays;

@CommandAlias("hologram|holo")
@CommandPermission("trynocs.holograms.hologram")
public class Hologram extends BaseCommand {
    private Configmanager configmanager = main.getPlugin().getConfigManager();

    @Subcommand("create")
    public void onExecute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            String name;
            if (args.length == 0) {
                name = player.getName();
            } else {
                name = main.translateColors(String.join(" ", args));
            }

            FileConfiguration config = configmanager.getCustomConfig("holograms");
            int id = config.getInt("holograms.total") + 1;
            config.set("holograms." + id + ".name", name);
            config.set("holograms.total", id);

            ArmorStand armorStand  = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
            armorStand.setVisible(false);
            armorStand.setRemoveWhenFarAway(false);
            armorStand.setGravity(false);
            armorStand.setCustomName(name.replace("{newline}", "\n"));
            armorStand.setCustomNameVisible(true);

            // Set metadata to identify the armor stand with the hologram ID
            armorStand.setMetadata("hologramID", new FixedMetadataValue(main.getPlugin(), String.valueOf(id)));

            configmanager.saveCustomConfig("holograms");
        }
    }



    @Subcommand("delete")
    @CommandPermission("trynocs.holograms.hologram.delete")
    public void onDelete(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                sender.sendMessage(main.prefix + "§cPlease provide the hologram name or ID to delete.");
                return;
            }

            String nameOrId = main.translateColors(String.join(" ", args));
            FileConfiguration config = configmanager.getCustomConfig("holograms");
            boolean found = false;

            for (String id : config.getConfigurationSection("holograms").getKeys(false)) {
                if (!id.equalsIgnoreCase("total")) {
                    String holoName = config.getString("holograms." + id + ".name");

                    if (holoName.equalsIgnoreCase(nameOrId) || id.equals(nameOrId)) {
                        for (ArmorStand armorStand : player.getWorld().getEntitiesByClass(ArmorStand.class)) {
                            // Check if armor stand has the "hologramID" metadata
                            if (armorStand.hasMetadata("hologramID")) {
                                for (MetadataValue metadataValue : armorStand.getMetadata("hologramID")) {
                                    if (metadataValue.asString().equals(id)) {
                                        armorStand.remove();
                                        sender.sendMessage(main.prefix + "§aHologram '" + holoName + "§a' (ID: " + id + ") has been removed.");
                                        found = true;
                                        break;
                                    }
                                }
                            }
                        }

                        if (found) {
                            config.set("holograms." + id, null);
                            config.set("holograms.total", config.getInt("holograms.total") - 1);
                            configmanager.saveCustomConfig("holograms");
                        }
                        break;
                    }
                }
            }

            if (!found) sender.sendMessage(main.prefix + "§cHologram with name or ID '" + nameOrId + "§c' not found.");
        } else sender.sendMessage(main.prefix + "§cOnly players can execute this command.");
    }

    @Subcommand("edit")
    @CommandPermission("trynocs.holograms.hologram.edit")
    public void onEdit(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length < 2) {
                sender.sendMessage(main.prefix + "§cPlease provide the hologram name or ID to edit, followed by the new name.");
                return;
            }

            String nameOrId = main.translateColors(args[0]);
            String newName = main.translateColors(String.join(" ", Arrays.copyOfRange(args, 1, args.length)).replace("{newline}", "\n"));

            FileConfiguration config = configmanager.getCustomConfig("holograms");
            boolean found = false;

            for (String id : config.getConfigurationSection("holograms").getKeys(false)) {
                if (!id.equalsIgnoreCase("total")) {
                    String holoName = config.getString("holograms." + id + ".name");

                    if (holoName.equalsIgnoreCase(nameOrId) || id.equals(nameOrId)) {
                        for (ArmorStand armorStand : player.getWorld().getEntitiesByClass(ArmorStand.class)) {
                            // Check if armor stand has the "hologramID" metadata
                            if (armorStand.hasMetadata("hologramID")) {
                                for (MetadataValue metadataValue : armorStand.getMetadata("hologramID")) {
                                    if (metadataValue.asString().equals(id)) {
                                        armorStand.setCustomName(newName);  // Set the new name
                                        sender.sendMessage(main.prefix + "§aHologram '" + holoName + "§a' (ID: " + id + ") has been renamed to '" + newName + "§a'.");
                                        found = true;
                                        break;
                                    }
                                }
                            }
                        }

                        if (found) {
                            config.set("holograms." + id + ".name", newName);  // Update the name in the config
                            configmanager.saveCustomConfig("holograms");
                        }
                        break;
                    }
                }
            }

            if (!found) sender.sendMessage(main.prefix + "§cHologram with name or ID '" + nameOrId + "§c' not found.");
        } else {
            sender.sendMessage(main.prefix + "§cOnly players can execute this command.");
        }
    }




}
