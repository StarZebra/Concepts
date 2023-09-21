package me.starzebra.concepts.listeners;

import me.starzebra.concepts.Concepts;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DiamondHomeItem implements Listener {

    static Plugin plugin = Concepts.getPlugin();

    @EventHandler
    public void onClick(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if(event.getAction() == Action.LEFT_CLICK_AIR && player.getInventory().getItemInMainHand().getType() == Material.DIAMOND){
            particleLine(player);
        }
    }

    public static void particleLine(Player player){
        Location startLoc = player.getEyeLocation();

        Location particleLoc = startLoc.clone();

        World world = startLoc.getWorld();


        new BukkitRunnable(){
            int maxBeamLength = 30;
            int beamLength = 0;

            public void run() {

                particleLoc.getNearbyEntities(5,5,5).forEach(entity -> {
                    if(!(entity instanceof LivingEntity)) return;
                    if(entity == player) return;


                    if (entity.getBoundingBox().contains(particleLoc.toVector())) {
                        world.spawnParticle(Particle.FLASH, particleLoc, 0);
                        world.playSound(particleLoc, Sound.ENTITY_ALLAY_DEATH, 2, 1);

                        entity.setVelocity(entity.getVelocity().add(particleLoc.getDirection().normalize().multiply(1.5)));

                        ((LivingEntity) entity).damage(5, player);
                        this.cancel();
                    }

                });

                    Vector vecOffset = particleLoc.getDirection().clone().multiply(0.5);

                    beamLength++;

                    if (beamLength >= maxBeamLength) {
                        world.spawnParticle(Particle.FLASH, particleLoc, 0);
                        this.cancel();
                        return;
                    }

                    particleLoc.add(vecOffset);

                    world.spawnParticle(Particle.END_ROD, particleLoc, 0);
                }

        }.runTaskTimer(plugin, 0, 1);
    }
}
