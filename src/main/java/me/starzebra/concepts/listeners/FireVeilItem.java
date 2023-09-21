package me.starzebra.concepts.listeners;

import me.starzebra.concepts.Concepts;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class FireVeilItem implements Listener {

    boolean onCooldown = false;

    int mobsKilled = 0;

    @EventHandler
    public void onRightClick(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(!player.getInventory().getItemInMainHand().getType().equals(Material.BLAZE_POWDER)) return;
        if(event.getHand() == EquipmentSlot.OFF_HAND) return;
        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;




        if(onCooldown){
            player.sendMessage(Component.text("§cItem is currently on cooldown."));
            return;
        }

        onCooldown = true;

        new BukkitRunnable() {
            @Override
            public void run() {
                onCooldown = false;
            }
        }.runTaskLaterAsynchronously(Concepts.getPlugin(), 100L);


        World world = player.getWorld();
        world.playSound(player, Sound.ITEM_FLINTANDSTEEL_USE, 1, .2f);

        BukkitTask fireVeilTask = new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = player.getLocation();
                for (double i = 0; i < 7; i+=1.5){
                    makeCircle(world, loc, 2.5, i);

                    Location checkLoc = player.getLocation();
                    for (Entity entity : checkLoc.getNearbyEntities(2.5, 2.5, 2.5)) {
                        if(entity instanceof Player) continue;
                        if(entity.getLocation().distance(checkLoc) >= 2.5) continue;
                        entity.remove();
                        mobsKilled++;
                    }
                }
            }
        }.runTaskTimer(Concepts.getPlugin(), 0, 3L);

        new BukkitRunnable() {
            @Override
            public void run() {
                fireVeilTask.cancel();
                player.sendMessage(Component.text("§eYour §6Fire Veil§e killed §6"+mobsKilled+ " §emobs."));
                mobsKilled = 0;
            }
        }.runTaskLater(Concepts.getPlugin(), 100L);
    }

    private void makeCircle(World world, Location location, double radius, double yOffset){
        for(int i = 0; i < 360; i+=30){
            double radians = Math.toRadians(i);
            double x = Math.cos(radians);
            double z = Math.sin(radians);
            Location pos = location.clone().add(x*radius,0+yOffset,z*radius);

            world.spawnParticle(Particle.FLAME, pos, 0);
        }
    }
}
