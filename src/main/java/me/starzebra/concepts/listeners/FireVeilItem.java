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
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class FireVeilItem implements Listener {

    Plugin plugin = Concepts.getPlugin();

    boolean onCooldown = false;

    int mobsKilled = 0;
    double circleRadius = 2.5;
    int cylHeight = 6;

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
        }.runTaskLaterAsynchronously(plugin, 100L);

        World world = player.getWorld();
        world.playSound(player, Sound.ITEM_FLINTANDSTEEL_USE, 1, .2f);

        BukkitTask fireVeilTask = new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = player.getLocation();
                for (double i = 0; i <=cylHeight; i+=1.5){
                    makeCircle(world, loc, circleRadius, i);
                    getEntitiesInCyl(player, circleRadius).forEach( (entity -> {
                        entity.remove();
                        mobsKilled++;
                    }));
                }
            }
        }.runTaskTimer(plugin, 0, 2L);

        new BukkitRunnable() {
            @Override
            public void run() {
                fireVeilTask.cancel();
                if(mobsKilled <= 0) return;
                player.sendMessage(Component.text("§eYour §6Fire Veil§e killed §6"+mobsKilled+ " §emobs."));
                mobsKilled = 0;
            }
        }.runTaskLater(plugin, 100L);
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

    private ArrayList<Entity> getEntitiesInCyl(Player player, double radius){
        ArrayList<Entity> stmt = new ArrayList<>();
        Location playerLoc = player.getLocation();

        Location maxCylCenter = playerLoc.clone().add(0,cylHeight,0);

        for(Entity entity : player.getNearbyEntities(radius, radius, radius)){
            //Check if entity is in cylinder
            if(entity.getBoundingBox().getMinY() > maxCylCenter.getY()) continue;
            if(entity.getBoundingBox().getMaxY() < playerLoc.getY()) continue;

            double entityX = entity.getLocation().getX();
            double entityZ = entity.getLocation().getZ();

            double distance = Math.sqrt(Math.pow(entityX - playerLoc.getX(), 2) + Math.pow(entityZ - playerLoc.getZ(), 2));

            if(distance <= radius){
                stmt.add(entity);
            }

        }
        return stmt;
    }

}
