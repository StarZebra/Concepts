package me.starzebra.concepts.listeners;

import me.starzebra.concepts.Concepts;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class TPItem implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent event){

        Player player = event.getPlayer();
        World world = player.getWorld();
        final float offset = 0.1f;
        final int tpDistance = 12;
        if(!player.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_SHOVEL)) return;
        if(event.getHand() == EquipmentSlot.OFF_HAND) return;
        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;
        if(player.isSneaking()) {
            RayTraceResult result = world
                    .rayTraceBlocks(player.getEyeLocation(), player.getEyeLocation().getDirection().normalize(), 60.0d, FluidCollisionMode.ALWAYS, true);
            if(result == null || result.getHitBlock() == null) return;
            Location blockLoc = result.getHitBlock().getLocation();
            Location tpLocation = new Location(world, blockLoc.getX(), blockLoc.getY(), blockLoc.getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
            tpLocation.add(new Vector(0.5, 1, 0.5));

            //Checking if blocks above tp location is air
            if(!blockCheck(result.getHitBlock().getLocation(), world)){
                player.sendActionBar(Component.text("§cThere are blocks in the way!"));
                return;
            }

            //Teleporting the player 1 tick later because of minecraft silly block click functionality triggering the click twice
            new BukkitRunnable(){
                @Override
                public void run(){
                    if(player.getLocation().distance(tpLocation) > tpDistance){
                        spawnParticle(world, player.getLocation(), offset);
                    }
                    player.teleport(tpLocation);
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 1, 0.1f);
                }
            }.runTaskLater(Concepts.getPlugin(), 1L);

        }else if(!player.isSneaking()){

            BlockIterator iter = new BlockIterator(player, tpDistance);
            Block lastBlock;
            Block prevBlock;
            for(prevBlock = null; iter.hasNext(); prevBlock = lastBlock){
                lastBlock = iter.next();
                if(lastBlock.getType() != Material.AIR) break;
            }

            Block finalPrevBlock = prevBlock;
            if(finalPrevBlock == null) return;

            Location finalLoc = new Location(world, finalPrevBlock.getX(), finalPrevBlock.getY(), finalPrevBlock.getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());

            new BukkitRunnable(){
                @Override
                public void run(){
                    player.teleport(finalLoc.add(0.5d,0,0.5d));
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.7f, 1f);
                }
            }.runTaskLater(Concepts.getPlugin(), 1L);
        }

    }

    private static void spawnParticle(World world, Location loc, float offset){
        world.spawnParticle(Particle.REDSTONE, loc, 3, offset, offset*1.5, offset, new Particle.DustOptions(Color.BLACK, 2.0f));
        world.spawnParticle(Particle.REDSTONE, loc, 3, offset, offset, offset, new Particle.DustOptions(Color.PURPLE, 2.0f));
        world.spawnParticle(Particle.REDSTONE, loc, 2, offset, offset*2, offset, new Particle.DustOptions(Color.GRAY, 1.0f));
    }

    private static boolean blockCheck(Location startLoc, World world){
        for (int i = 0; i < 2; i++) {
            startLoc.add(0,1,0);
            if(world.getBlockAt(startLoc).getType() != Material.AIR){
                return false;
            }
        }
        return true;
    }

}
