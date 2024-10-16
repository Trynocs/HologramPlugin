package com.trynocs.hologramPlugin;

import co.aikar.commands.PaperCommandManager;
import com.trynocs.hologramPlugin.commands.Hologram;
import com.trynocs.hologramPlugin.utils.ItemBuilder;
import com.trynocs.hologramPlugin.utils.config.Configmanager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;

public final class main extends JavaPlugin {

    public static main plugin;

    public static String prefix;
    public static String noperm;
    public static String beplayer;
    public static String noplayer;

    private Configmanager configManager;
    private PaperCommandManager commandManager;
    private PluginManager pluginManager;

    @Override
    public void onEnable() {
        getLogger().info("");
        getLogger().info("██╗████████╗");
        getLogger().info("██║╚══██╔══╝");
        getLogger().info("██║░░░██║░░░");
        getLogger().info("██║░░░██║░░░");
        getLogger().info("██║░░░██║░░░");
        getLogger().info("╚═╝░░░╚═╝░░░");
        getLogger().info("");
        getLogger().info("Plugin starting...");

        plugin = this;
        configManager = new Configmanager(this);
        pluginManager = Bukkit.getPluginManager();
        commandManager = new PaperCommandManager(this);
        configManager.saveDefaultConfig();
        configManager.saveCustomConfig("holograms");
        configManager.saveDefaultConfig();

        loadConfigValues();
        register();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void register() {
        // Command & Event/Listener Registrierung
        commandManager.registerCommand(new Hologram());
    }

    private void loadConfigValues() {
        String prefix2 = configManager.getConfig().getString("messages.prefix", "&b&lHologram &8» &7");
        prefix = ChatColor.translateAlternateColorCodes('&', prefix2);
    }

    public static main getPlugin() {
        return plugin;
    }

    public Configmanager getConfigManager() {
        return configManager;
    }

    public static String translateColors(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> translateColors(List<String> texts) {
        return texts.stream().map(main::translateColors).collect(Collectors.toList());
    }

    public ItemStack getPlacerholderItem() {
        return new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("§7").build();
    }
}
