package me.starzebra.concepts;

import me.starzebra.concepts.listeners.*;
import me.starzebra.concepts.commands.DisplayCurveCommand;
import me.starzebra.concepts.commands.ThingCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;

public final class Concepts extends JavaPlugin {

    private static Concepts plugin;

    public static Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {

        plugin = this;
        getLogger().severe("This is totally fine (real) trust me bro.");

        getServer().getPluginManager().registerEvents(new DeathEventListener(), this);
        getServer().getCommandMap().register("thing", new ThingCommand("thing"));
        getServer().getCommandMap().register("display", new DisplayCurveCommand("display"));
        getServer().getPluginManager().registerEvents(new DiamondHomeItem(), this);
        getServer().getPluginManager().registerEvents(new CurveToolItem(), this);
        getServer().getPluginManager().registerEvents(new CircleItem(), this);
        getServer().getPluginManager().registerEvents(new TPItem(), this);

        //Auto register any Listener class
        String packageName = getClass().getPackage().getName();

        for (Class<?> clazz : new Reflections(packageName+ ".listeners").getSubTypesOf(Listener.class)){
            try {

                Listener listener = (Listener) clazz.getDeclaredConstructor().newInstance();
                getServer().getPluginManager().registerEvents(listener, this);

            } catch (InvocationTargetException | InstantiationException | NoSuchMethodException |
                     IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }



    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().fine("This is NOT fine, why I get disabled!!!! >:(");
    }


}
