package tasks;

import listeners.SuperMapListener;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import plugin.SuperMap;
 
public class DelayedUnroll extends BukkitRunnable {
    private final JavaPlugin plugin;
 
    public DelayedUnroll(JavaPlugin plugin,ItemStack i, Player p,int slot,SuperMapListener sml) {
        this.plugin = plugin;
        item=i;
        this.slot=slot;
        player=p;
        superMapListenInstance=sml;
    }
 
    public void run() {
        PlayerInventory inventory=player.getInventory();
        inventory.clear(slot);
                inventory.setItem(slot, item);
                superMapListenInstance.getCzc().addPlayer(player);
                SuperMap.mapCount.put(player.getDisplayName(), SuperMap.mapCount.get(player.getDisplayName())+1);
                if(SuperMap.numCartMaps<1){
                        superMapListenInstance.reenableczc();
                }
                SuperMap.numCartMaps++;
    }
    private SuperMapListener superMapListenInstance;
    private Player player;
    private int slot;
    private ItemStack item;
}
