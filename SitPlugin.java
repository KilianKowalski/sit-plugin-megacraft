package com.example.sitplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class SitPlugin extends JavaPlugin implements Listener {

    private final HashMap<UUID, ArmorStand> sittingPlayers = new HashMap<>();

    @Override
    public void onEnable() {
        getCommand("sit").setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only player can use this command.");
                return true;
            }

            if (sittingPlayers.containsKey(player.getUniqueId())) {
                player.sendMessage("You're already sitting!");
                return true;
            }

            Location loc = player.getLocation();
            loc.setY(loc.getY() - 1.2);
            ArmorStand armorStand = loc.getWorld().spawn(loc, ArmorStand.class);

            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setInvulnerable(true);
            armorStand.setMarker(true);

            armorStand.addPassenger(player);
            sittingPlayers.put(player.getUniqueId(), armorStand);

            player.sendMessage("Toggle Sneak to get up.");
            return true;
        });

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        sittingPlayers.values().forEach(ArmorStand::remove);
        sittingPlayers.clear();
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (sittingPlayers.containsKey(player.getUniqueId()) && player.isSneaking()) {
            ArmorStand armorStand = sittingPlayers.remove(player.getUniqueId());
            if (armorStand != null) {
                armorStand.remove();
            }
            player.sendMessage("You got up again.");
        }
    }
}
