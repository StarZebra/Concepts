package me.starzebra.concepts.listeners;

import me.starzebra.concepts.Concepts;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.stream.IntStream;

public class GameJoinEvent implements Listener {

    Plugin plugin = Concepts.getPlugin();

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(player.hasPlayedBefore()) return;

        ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Location playerLoc = player.getLocation();
            Location inFront = playerLoc.clone().add(playerLoc.getDirection().multiply(4));

            Block block = inFront.getBlock();
            if(block.getType() != Material.AIR){
                double ground = IntStream.range(0, (int) Math.ceil(4))
                        .mapToObj(dy -> block.getRelative(0, dy, 0))
                        .filter(b -> b.getType() == Material.AIR)
                        .findFirst().orElse(block).getY();

                inFront.setY(ground);
            }
            stand.teleport(inFront);
        }, 1L, 1L);

    }
}
