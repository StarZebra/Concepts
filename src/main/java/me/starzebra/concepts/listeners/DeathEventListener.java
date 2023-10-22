package me.starzebra.concepts.listeners;

import com.mojang.math.Transformation;
import me.starzebra.concepts.Concepts;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class DeathEventListener implements Listener {

    Plugin plugin = Concepts.getPlugin();


    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        Player player = event.getEntity().getKiller();
        if(player == null) return;
        ServerLevel level = ((CraftWorld) player.getWorld()).getHandle();

        List<ItemStack> droppedItems = event.getDrops();
        List<ItemStack> newList = new ArrayList<>();
        droppedItems.forEach(drop -> newList.add(drop.clone()));
        event.getDrops().clear();

        Location droppedLocation = event.getEntity().getLocation();



        //BlockDisplay display = (BlockDisplay) player.getWorld().spawnEntity(droppedLocation.add(0,1,0), EntityType.BLOCK_DISPLAY);
        if(newList.isEmpty()) return;

        int total = 0;
        for(ItemStack item : newList){
            total += item.getAmount();

        }

        float sizeThingyMajig = (float) total /16 +.4f;


        Display.BlockDisplay nmsBlockDisplay = EntityType.BLOCK_DISPLAY.create(level);
        if(nmsBlockDisplay == null) return;
        Vec3 vecPos = new Vec3(droppedLocation.getX(), droppedLocation.getY(), droppedLocation.getZ());
        nmsBlockDisplay.moveTo(vecPos.add(0,1,0));
        Transformation transformation = new Transformation(new Vector3f(-sizeThingyMajig/2,0,-sizeThingyMajig/2), new Quaternionf(0,0,0,1), new Vector3f(sizeThingyMajig, sizeThingyMajig, sizeThingyMajig), new Quaternionf(0,0,0,1));
        nmsBlockDisplay.setTransformation(transformation);
        nmsBlockDisplay.setBlockState(Blocks.CHEST.defaultBlockState());

        level.addFreshEntity(nmsBlockDisplay, CreatureSpawnEvent.SpawnReason.CUSTOM);

        //player.sendMessage(Component.text("Spawned Block Display at "+nmsBlockDisplay.getBukkitEntity().getLocation()));




        Display.TextDisplay nmsDisplay = EntityType.TEXT_DISPLAY.create(level);

        if(nmsDisplay == null) return;
        //Transformation startTransform = new Transformation(new Vector3f(-sizeThingyMajig/2,0,-sizeThingyMajig/2), new Quaternionf(0,0,0,1), new Vector3f(sizeThingyMajig, sizeThingyMajig, sizeThingyMajig), new Quaternionf(0,0,0,1));
        Vec3 textPos = new Vec3(nmsBlockDisplay.getEyePosition().x,nmsBlockDisplay.getEyePosition().y+sizeThingyMajig, nmsBlockDisplay.getEyePosition().z);
        nmsDisplay.moveTo(textPos);
        nmsDisplay.setText(net.minecraft.network.chat.Component.translatable("§6" + stackToHumanReadable(newList)));
        nmsDisplay.setFlags((byte)2);
        nmsDisplay.setBillboardConstraints(Display.BillboardConstraints.CENTER);
        //nmsDisplay.getBukkitEntity().setMetadata("background", new FixedMetadataValue(plugin, 0));

        level.addFreshEntity(nmsDisplay, CreatureSpawnEvent.SpawnReason.CUSTOM);




        // Transformation(translation, leftrotation, scale, rightrotation)
        //TextDisplay textDisplay = (TextDisplay) display.getWorld().spawnEntity(display.getLocation().add(0,sizeThingyMajig,0), EntityType.TEXT_DISPLAY);
//        textDisplay.text(Component.text("§6" + stackToHumanReadable(newList)));
//        textDisplay.setSeeThrough(false);
//        textDisplay.setBillboard(Display.Billboard.CENTER);
//        textDisplay.setBackgroundColor(Color.fromARGB(0, 1,1,1));
//        display.setTransformation(startTransform);
//        display.setBlock(Bukkit.createBlockData(Material.CHEST));

        new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = nmsBlockDisplay.getBukkitEntity().getLocation();

                nmsBlockDisplay.setYRot(nmsBlockDisplay.getYRot()-5);

                //nmsBlockDisplay.teleport(new Location(display.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw()-5, loc.getPitch()));

                if(player.getEyeLocation().distanceSquared(loc) < nmsBlockDisplay.getBbHeight()+1.5){
                    newList.forEach(drop -> {
                        player.getInventory().addItem(drop);
                        player.getWorld().playSound(droppedLocation, Sound.ENTITY_ITEM_PICKUP, 1, 1);
                    });
                    nmsBlockDisplay.kill();
                    nmsDisplay.kill();
                    cancel();

                }
            }
        }.runTaskTimer(plugin, 0, 1L);

    }

    private static ArrayList<String> stackToHumanReadable(List<ItemStack> stackList){
        ArrayList<String> returnList = new ArrayList<>();
        stackList.forEach(stack -> {
            String readableString = stack.toString().replace("ItemStack{", "").replace("}", "");
            String[] splitString = readableString.replace(" ", "").replace("_", " ").split("x");
            String material = splitString[0];
            String amount = splitString[1];
            returnList.add(amount+"x " + material);
        });
        return returnList;
    }
}
