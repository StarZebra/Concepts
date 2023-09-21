package me.starzebra.concepts.commands;

import me.starzebra.concepts.listeners.CurveToolItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DisplayCurveCommand extends Command {
    public DisplayCurveCommand(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof Player){
            Player player = ((Player) sender).getPlayer();

            assert player != null;
            Location p0 = new Location(player.getWorld(), 34, 65, -74);
            Location p1 = CurveToolItem.curveDefiner;
            Location p2 = new Location(player.getWorld(), 34, 66, -67);

            if(p1 == null) { sender.sendMessage(Component.text("Uhm yeah p1 is null bozo")); return false; }

            List<Location> curve = CurveToolItem.bezierCurve(10, p0,p1,p2);;
            for (Location point : curve) {
                //player.getWorld().spawnParticle(Particle.END_ROD, point, 0);
                Slime slime = (Slime) player.getWorld().spawnEntity(point, EntityType.SLIME);
                slime.setSize(2);
                slime.setWander(false);
                slime.setGravity(false);
                slime.setAI(false);

            }
        }

        return false;
    }
}
