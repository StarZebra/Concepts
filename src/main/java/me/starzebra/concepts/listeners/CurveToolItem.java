package me.starzebra.concepts.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class CurveToolItem implements Listener {

    public static Location curveDefiner = null;

    @EventHandler
    public void onClick(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(event.getAction() == Action.RIGHT_CLICK_AIR && player.getInventory().getItemInMainHand().getType() == Material.STICK){

            curveDefiner = player.getTargetBlock(null, 10).getLocation();

            player.sendMessage(Component.text("Set curve to: " +curveDefiner));

        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event){
        if(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SLIME_SPLIT){
            event.setCancelled(true);

        }
    }

    public static Location bezierPoint(float t, Location p0, Location p1, Location p2){
        float a = (1-t)*(1-t);
        float b = 2*(1-t)*t;
        float c= t*t;

        return p0.clone().multiply(a).add(p1.clone().multiply(b)).add(p2.clone().multiply(c));
    }

    public static List<Location> bezierCurve(int count, Location p0, Location p1, Location p2){
        List<Location> points = new ArrayList<>();
        for(int i =1; i< count; i++){
            float t = i / (float) count;
            points.add(bezierPoint(t, p0,p1,p2));
        }
        return points;
    }
}
