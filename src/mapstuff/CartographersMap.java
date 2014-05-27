package mapstuff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import listeners.SuperMapListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView.Scale;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.SuperMap;
import tasks.DelayedUnroll;

public class CartographersMap implements Serializable{
        private static final long serialVersionUID = 1L;//honestly i don't know what this means...eclipse thought i needed it though...
        public CartographersMap(){
                map= new ItemStack(Material.EMPTY_MAP, 1);
                List<String> ls=new ArrayList<String>();
                map.setDurability(getEmptyId());
                ls.add("Map #"+map.getDurability());
                setName(map,"Empty Cartographer's Map",(ls));
                mapViewArray=new ArrayList<FakeMapView>();
                SuperMap.mapItemToCMap.put(new Integer(map.getDurability()), this);
                mapIndex=new HashMap<String,Integer>();
        }
        public CartographersMap(CartographersMap cart){
                map=cart.getMap();
                List<String> ls=new ArrayList<String>();
                map.setDurability(cart.map.getDurability());
                ls.add("Map #"+map.getDurability());
                setName(map,"Empty Cartographer's Map",(ls));
                mapViewArray=cart.mapViewArray;
                mapIndex=cart.mapIndex;
        }
        public CartographersMap(Player p){//this is just for testing purposes. I use it to give the player a map with a command.
                CartographersMap cm=new CartographersMap();
                PlayerInventory inventory = p.getInventory();
                inventory.addItem(cm.map);
        }
        public CartographersMap(ItemStack map){
                this.map= map;
                List<String> ls=new ArrayList<String>();
                map.setDurability(getEmptyId());
                ls.add("Map #"+map.getDurability());
                setName(map,"Empty Cartographer's Map",(ls));
                mapViewArray=new ArrayList<FakeMapView>();
                SuperMap.mapItemToCMap.put(new Integer(map.getDurability()), this);
                mapIndex=new HashMap<String,Integer>();
        }
        public CartographersMap(Player p,ItemStack map,SuperMapListener sml){//directly associate a map with
                mapViewArray=new ArrayList<FakeMapView>();
                mapIndex=new HashMap<String,Integer>();
                this.map= map;
                List<String> ls=new ArrayList<String>();
                ls.add("Shift+Right Click to disassemble");
                setName(map,"Cartographer's Map",ls);
                //it's finally time to make the mapViewArray
                FakeMapView temp;
                Location center=p.getLocation();
                for(int i=-1;i<2;i++){
                        for(int j=-1;j<2;j++){
                                temp=new FakeMapView(p.getWorld());
                                temp.setScale(Scale.FARTHEST);
                                temp.setCenterX((int)(center.getX()+j*MAPLENGTH));
                                temp.setCenterZ((int)(center.getZ()+i*MAPLENGTH));
                                mapViewArray.add(temp);
                        }
                }
                map.setDurability(mapViewArray.get(4).getId());
                for(int i=0;i<9;i++){
                        SuperMap.mapItemToCMap.put(new Integer(mapViewArray.get(i).getId()), this);
                }
                mapIndex.put(p.getDisplayName(), 4);//lol just four, it's the middle one and i want to start there every time
                sml.getCzc().addPlayer(p);
                SuperMap.mapCount.put(p.getDisplayName(), SuperMap.mapCount.get(p.getDisplayName())+1);
                if(SuperMap.numCartMaps<1){
                        sml.reenableczc();
                }
                SuperMap.numCartMaps++;
                associatePlayer(p);
        }
        public void associatePlayer(Player p){
                mapIndex.put(p.getDisplayName(), 4);
                mapCheck(p.getLocation(),p);
        }
        private void setName(ItemStack is, String name, List<String> lore){
                ItemMeta im=is.getItemMeta();
                if(name!=null){
                        im.setDisplayName(name);
                }
                if(lore!=null){
                        im.setLore(lore);
                }
                is.setItemMeta(im);
        }
        public void unroll(int slot,Player p,Plugin plug,SuperMapListener sml) {
                //first make the map visually pretty and make it say cartographer's map
                map= new ItemStack(Material.MAP, 1);
                List<String> ls=new ArrayList<String>();
                ls.add("Shift+Right Click to disassemble");
                setName(map,"Cartographer's Map",ls);
                //it's finally time to make the mapViewArray
                FakeMapView temp;
                Location center=p.getLocation();
                for(int i=-1;i<2;i++){
                        for(int j=-1;j<2;j++){
                                temp=new FakeMapView(p.getWorld());
                                temp.setScale(Scale.FARTHEST);
                                temp.setCenterX((int)(center.getX()+j*MAPLENGTH));
                                temp.setCenterZ((int)(center.getZ()+i*MAPLENGTH));
                                mapViewArray.add(temp);
                        }
                }
                map.setDurability(mapViewArray.get(4).getId());
                for(int i=0;i<9;i++){
                        SuperMap.mapItemToCMap.put(new Integer(mapViewArray.get(i).getId()), this);
                }
                mapIndex.put(p.getDisplayName(), 4);//lol just four, it's the middle one and i want to start there every time
                new DelayedUnroll((JavaPlugin) plug,map,p,slot,sml).runTaskLater(plug, 1);
        }
        public void disassemble(Player p) {
                ItemStack map0=new ItemStack(Material.MAP,1);
                ItemStack map1=new ItemStack(Material.MAP,1);
                ItemStack map2=new ItemStack(Material.MAP,1);
                ItemStack map3=new ItemStack(Material.MAP,1);
                ItemStack map4=new ItemStack(Material.MAP,1);
                ItemStack map5=new ItemStack(Material.MAP,1);
                ItemStack map6=new ItemStack(Material.MAP,1);
                ItemStack map7=new ItemStack(Material.MAP,1);
                ItemStack map8=new ItemStack(Material.MAP,1);
                map0.setDurability(mapViewArray.get(0).getId());
                map1.setDurability(mapViewArray.get(1).getId());
                map2.setDurability(mapViewArray.get(2).getId());
                map3.setDurability(mapViewArray.get(3).getId());
                map4.setDurability(mapViewArray.get(4).getId());
                map5.setDurability(mapViewArray.get(5).getId());
                map6.setDurability(mapViewArray.get(6).getId());
                map7.setDurability(mapViewArray.get(7).getId());
                map8.setDurability(mapViewArray.get(8).getId());
                p.getWorld().dropItem(p.getLocation(), map0);
                p.getWorld().dropItem(p.getLocation(), map1);
                p.getWorld().dropItem(p.getLocation(), map2);
                p.getWorld().dropItem(p.getLocation(), map3);
                p.getWorld().dropItem(p.getLocation(), map4);
                p.getWorld().dropItem(p.getLocation(), map5);
                p.getWorld().dropItem(p.getLocation(), map6);
                p.getWorld().dropItem(p.getLocation(), map7);
                p.getWorld().dropItem(p.getLocation(), map8);
                //think i'm gonna leave nulling this variable's refrences to the outside code because i don't want too many points accessing that code
        }
        private boolean isInZone(Location pl,Player p){
                //compares player's location with the zone he's supposed to be in //pl stands for player location
                if((pl.getBlockX()>getCenterX(p)+HALFLENGTH)||(pl.getBlockZ()>getCenterZ(p)+HALFLENGTH)||(pl.getBlockX()<getCenterX(p)-HALFLENGTH)||(pl.getBlockZ()<getCenterZ(p)-HALFLENGTH)){
                        return false;
                }
                return true;
        }
        private boolean isInMapSpace(Location pl){
                //sees if the player is even in any of the damn maps ie. is he within 512*3 blocks from the center //pl stands for player location
                if((pl.getBlockX()>getCenter().getBlockX()+HALFLENGTH*3)||(pl.getBlockZ()>getCenter().getBlockZ()+HALFLENGTH*3)||(pl.getBlockX()<getCenter().getBlockX()-HALFLENGTH*3)||(pl.getBlockZ()<getCenter().getBlockZ()-HALFLENGTH*3)){
                        return false;
                }
                return true;
        }
        public static short getEmptyId(){
                return emptyId++;//return then increment
        }
        public Location getCenter(){
                return new Location(null,mapViewArray.get(4).getCenterX(),0,mapViewArray.get(4).getCenterZ());
        }
        public int getCenterX(Player p){
                return mapViewArray.get(mapIndex.get(p.getDisplayName())).getCenterX();
        }
        public int getCenterZ(Player p){
                return mapViewArray.get(mapIndex.get(p.getDisplayName())).getCenterZ();
        }
        public void mapCheck(Location pl,Player p){//called from the constantZoneCheck function
                if(!mapIndex.containsKey(p.getDisplayName())){
                        mapIndex.put(p.getDisplayName(), 4);
                }
                if(isInZone(pl,p)){
                        return;
                }else if(isInMapSpace(pl)){
                        //this next section uses some math to get the index of wherever they are currently at without iterating through nine if then statements
                        int x=pl.getBlockX()-mapViewArray.get(4).getCenterX();
                        int z=pl.getBlockZ()-mapViewArray.get(4).getCenterZ();
                        x/=HALFLENGTH;
                        z/=HALFLENGTH;
                        int index=SuperMap.translateCoordinates(x, z);
                        mapIndex.put(p.getDisplayName(), index);
                        //this is where i change maps finally!
                        changeMap(index,p);
                }else{
                        //stub
                        //this is where i check which map is closest to where they're located and set it.
                }
        }
        public ItemStack getMap(){
                return map;
        }
        private void changeMap(int index,Player p){
                p.getItemInHand().setDurability(mapViewArray.get(index).getId());
        }
        public static transient short emptyId;
        private transient ItemStack map;//represents the map or the empty map
        private HashMap<String,Integer> mapIndex;//between 0 and 8, these are the maps in the 3x3 array//now a hashmap that corresponds to individual players
        private ArrayList<FakeMapView> mapViewArray;
        private final int MAPLENGTH=2048;//we assume both x and z are the same so length refers to both
        private final int HALFLENGTH=MAPLENGTH/2;

}
