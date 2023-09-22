package me.starzebra.concepts.listeners;

import me.starzebra.concepts.Concepts;
import me.starzebra.concepts.utils.VectorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class CircleItem implements Listener {

    static Plugin plugin = Concepts.getPlugin();

    @EventHandler
    public void onClick(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(event.getAction() == Action.RIGHT_CLICK_AIR && player.getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD){
            player.sendActionBar(Component.text(player.getInventory().getItemInMainHand().getType().toString()));
            new BukkitRunnable(){

               final int points = 36;

               final Location loc = player.getEyeLocation();

               final World world = loc.getWorld();

               final Vector dir = player.getLocation().getDirection().normalize();

               final double increment = (2* Math.PI) / points;
               final double radius = 1;
               double offset = 0;

               int length = 10;

               final double pitch = toDegrees((loc.getPitch()+90.0F));
               final double yaw = -toDegrees(loc.getYaw());

               @Override
               public void run() {
                   length--;
                   if(length < 1){
                       this.cancel();
                       return;
                   }

                   for (int i = 0; i < points; i++){
                       double angle = i * increment + offset;
                       double x = radius * Math.cos(angle);
                       double z = radius * Math.sin(angle);

                       Vector vec = new Vector(x, 0, z);

                       VectorUtils.rotateAroundAxisX(vec, pitch);
                       VectorUtils.rotateAroundAxisY(vec, yaw);

                       loc.add(vec);
                       world.spawnParticle(Particle.FLAME, loc, 0);
                       loc.subtract(vec);

                   }
                   offset += increment/3;
                   if(offset >= increment){
                       offset=0;
                   }


                   loc.add(dir);

               }
           }.runTaskTimer(plugin, 0, 1);
        }
    }

    private float toDegrees(float radian){
        return radian*0.017453292f;
    }



}
