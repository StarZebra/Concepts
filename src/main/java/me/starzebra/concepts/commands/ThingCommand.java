package me.starzebra.concepts.commands;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class ThingCommand extends Command {

    public ThingCommand(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof Player){

            Location blockPos = ((Player) sender).getLocation();

            //Block block = blockPos.getBlock();

            for(int x = -2; x <= 2; x++){
                for(int y = -2; y <= 2; y++){
                    for(int z = -2; z <= 2; z++){
                        Location loc = new Location(((Player) sender).getWorld(), blockPos.getX()-x, blockPos.getY()-y, blockPos.getZ()-z);

                        //Location loc2 = new Location(((Player) sender).getWorld(), blockPos.getX()+x, blockPos.getY()+y, blockPos.getZ()+z);
                        if(loc.distance(blockPos) > 2){

                            ((Player) sender).getWorld().spawnParticle(Particle.END_ROD, loc, 0);

                        }

                        //((Player) sender).getWorld().spawnParticle(Particle.END_ROD, loc2, 0);
                    }
                }
            }





            return true;
        }

        return false;
    }
}
