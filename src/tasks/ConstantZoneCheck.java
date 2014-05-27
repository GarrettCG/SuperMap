package tasks;


import java.util.ArrayList;

import listeners.SuperMapListener;
import mapstuff.CartographersMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import plugin.SuperMap;
 
public class ConstantZoneCheck extends BukkitRunnable {
 

 
    public ConstantZoneCheck(JavaPlugin plugin,SuperMapListener sml) {
        this.plugin = plugin;
        this.sml=sml;
        playerList=new ArrayList<String>();
        enabled=true;
        dead=false;
    }
    private ConstantZoneCheck(JavaPlugin plugin, ArrayList<String> l){
        this.plugin=plugin;
        playerList=l;
    }
    public void disableMapCheck(){
        enabled=false;
    }
    public void enableMapCheck(){
        enabled=true;
    }
    public void run() {
        if(!enabled){
                cancel();
                dead=true;
        }
        Player p;
        for(String player:playerList){
                p=Bukkit.getPlayer(player);
                if(p.getItemInHand().hasItemMeta()&&p.getItemInHand().getType().equals(Material.MAP)){
                        if(p.getItemInHand().getItemMeta().getDisplayName().equals("Cartographer's Map")){
                                //this is where i do the zone checks
                                try{
                                        CartographersMap cm=SuperMap.mapItemToCMap.get(Integer.valueOf(p.getItemInHand().getDurability()));
                                        cm.mapCheck(p.getLocation(),p);
                                }catch(NullPointerException npe){
                                        CartographersMap cm=new CartographersMap(p,p.getItemInHand(),sml);
                                        cm.mapCheck(p.getLocation(), p);
                                }
                        }
                }
        }
    }
    public void addPlayer(Player p){
        if(!playerList.contains(p.getDisplayName())){
                playerList.add(p.getDisplayName());
        }
    }
    public void removePlayer(Player p){
        playerList.remove(p.getDisplayName());
    }
    public ConstantZoneCheck clone(){
        ConstantZoneCheck newczc =new ConstantZoneCheck(plugin,playerList);
        newczc.enabled=enabled;
        newczc.dead=false;
        return newczc;
    }
    public boolean isDead(){
        return dead;
    }
    private final JavaPlugin plugin;
        private SuperMapListener sml;
    private ArrayList <String> playerList;
    private boolean enabled,dead;
}
