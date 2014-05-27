package listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import mapstuff.CartographersMap;
import mapstuff.RecipeUtil;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import plugin.SuperMap;
import tasks.ConstantZoneCheck;

public class SuperMapListener implements Listener{
         private final SuperMap plugin;
            public SuperMapListener(SuperMap plugin) {

                plugin.getServer().getPluginManager().registerEvents(this, plugin);
                this.plugin = plugin;
                czc=new ConstantZoneCheck(plugin,this);
                ItemStack example=new ItemStack(Material.EMPTY_MAP);
                recipe=new ShapedRecipe(example);
                System.out.println("making a new recipe");
                recipe.shape(new String[]{"mmm","mmm","mmm"}).setIngredient('m',Material.EMPTY_MAP );
                Bukkit.addRecipe(recipe);
            }
            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent event) {
            	System.out.println("onPlayerJoin");
                int numMaps=0;
                PlayerInventory pi=event.getPlayer().getInventory();
                HashMap<Integer, ? extends ItemStack> hm=pi.all(Material.MAP);
                Iterator<? extends ItemStack> li=hm.values().iterator();
                SuperMap.regMapCount.put(event.getPlayer().getDisplayName(), hm.size());
                while(li.hasNext()){
                        ItemStack it=li.next();
                        if(it.hasItemMeta()){
                                if(it.getItemMeta().getDisplayName().equals("Cartographer's Map")){
                                        if(SuperMap.numCartMaps<1){
                                                reenableczc();
                                        }
                                        numMaps+=it.getAmount();
                                        czc.addPlayer(event.getPlayer());
                                }
                        }
                }
                SuperMap.numCartMaps+=numMaps;
                SuperMap.mapCount.put(event.getPlayer().getDisplayName(), new Integer(numMaps));
                System.out.println("Displaying all recipes");
                Iterator<Recipe> ri=Bukkit.recipeIterator();
                while(ri.hasNext()){
                	
                	Recipe r=ri.next();
                	if(r instanceof ShapedRecipe){
                		if(r.getResult().getType().equals(Material.EMPTY_MAP)){
                			System.out.println(((ShapedRecipe)r).getIngredientMap());
                		}
                	}
                }
            }
            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                SuperMap.numCartMaps-=SuperMap.mapCount.get(event.getPlayer().getDisplayName());
                SuperMap.mapCount.remove(event.getPlayer());
                czc.removePlayer(event.getPlayer());
                if(SuperMap.numCartMaps<1){
                        czc.disableMapCheck();
                }
            }
            @EventHandler
            public void onMapCraft(CraftItemEvent event) {
            	System.out.println("onMapCraft");
                if(event.getView().getItem(0).hasItemMeta()){
                        if(event.getView().getItem(0).getItemMeta().getDisplayName().equals("Cartographer's Map")){
                                if(event.getClick().isShiftClick()){
                                        event.setCancelled(true);
                                }
                        }
                }
            }
            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
            	System.out.println("onInventoryClick");
                if(event.getClick().isShiftClick()&&event.getClick().isRightClick()){
                        ItemStack item=event.getCurrentItem();
                        if(isCartoItem(item)){
                                event.setCancelled(true);
                                if(item.getAmount()<2){//basically if it's 1
                                        Player play=((Player)event.getWhoClicked());
                                        event.getView().setItem(event.getRawSlot(), null);
                                        SuperMap.numCartMaps--;
                                        SuperMap.regMapCount.put(play.getDisplayName(), SuperMap.regMapCount.get(play.getDisplayName())-1);
                                        SuperMap.mapCount.put(play.getDisplayName(), SuperMap.mapCount.get(play.getDisplayName())-1);
                                if(SuperMap.mapCount.get(play.getDisplayName())<1){//if it's zero...
                                        czc.removePlayer(play);
                                }
                                        if(SuperMap.numCartMaps<1){
                                                //this is where i disable the constantZoneCheck
                                                czc.disableMapCheck();
                                        }
                                        Integer mID=Integer.valueOf(item.getDurability());
                                        try{
                                                CartographersMap cm=SuperMap.mapItemToCMap.get(mID);
                                                cm.disassemble(play);
                                        }catch(NullPointerException npe){
                                                CartographersMap cm=new CartographersMap(play,item,this);
                                                cm.disassemble(play);
                                        }
                                        //SuperMap.mapItemToCMap.remove(mID);//i got rid of the deletion of the map on dissassemble since it can be cloned. keeping track of amounts is not worth it. the system i have now is much like the regular mapid save system where nothing is ever deleted.
                                }else{
                                        //this is where i need to write the code to handle the other condition ie. more than one map in the slot
                                        //currently, as uninplemented, it works but it just cancels out the event if you try to do it with more than one
                                }
                        }
                }
            }

            @EventHandler
            public void onInventoryClose(InventoryCloseEvent event){
                if(!(event.getPlayer() instanceof Player)){
                        return;
                }
                Player play=((Player) event.getPlayer());
               
                //if it doesn't contain the right amount of maps...
                int amount=getCartoMapAmount(play);
                if(amount!=SuperMap.mapCount.get(play.getDisplayName())){
                	//need to start czc or stop czc based on whether it crosses the 1 threshold
                	if(SuperMap.mapCount.get(play.getDisplayName())<amount){//adding maps
                		//get the amount thats added and store it as the difference
                		int difference=amount-SuperMap.mapCount.get(play.getDisplayName());//new amount-old amount
                		addingCartomap(play,difference);
                	}else if(SuperMap.mapCount.get(play.getDisplayName())>amount){//subtracting maps
                		int difference=SuperMap.mapCount.get(play.getDisplayName())-amount;//old amount-new amount
                		subtractingCartomap(play,difference);
                	}
                }
                SuperMap.regMapCount.put( play.getDisplayName(), getCartoMapAmount(play) );

            }
            @EventHandler
            public void onPreMapCraft(PrepareItemCraftEvent event) {
            	System.out.println("onPreMapCraft");
                if(RecipeUtil.areEqual(event.getRecipe(), recipe)){
                	System.out.println("recipes are equal");
                        CartographersMap tempMap=new CartographersMap();
                        event.getInventory().setResult(tempMap.getMap());
                }else if(isCarto(event.getInventory().getResult())){
                        if(!isRecipeCorrect(event.getInventory())){
                                event.getInventory().clear(0);
                        }else{//clone recipe is correct so we now have to set the lore(for some reason it doesn't stay like the custom name)
                                ItemStack i=event.getInventory().getResult();
                                ItemMeta im=i.getItemMeta();
                                List<String> ls=new ArrayList<String>();
                                ls.add("Shift+Right Click to disassemble");
                                im.setLore(ls);
                                i.setItemMeta(im);          
                        }
                }
            }
                @EventHandler
            public void onPlayerDeath(PlayerDeathEvent event) {
                SuperMap.numCartMaps-=SuperMap.mapCount.get(event.getEntity().getDisplayName());
                SuperMap.mapCount.put(event.getEntity().getDisplayName(),0);
                SuperMap.regMapCount.put(event.getEntity().getDisplayName(),0);
                if(SuperMap.mapCount.get(event.getEntity().getDisplayName())<1){
                        czc.removePlayer((Player)event.getEntity());
                }
                        if(SuperMap.numCartMaps<1){
                        //this is where i disable the constantZoneCheck
                                czc.disableMapCheck();
                        }

            }
            @EventHandler(priority = EventPriority.HIGHEST)
            public void onRightClick(PlayerInteractEvent event){
                if(!(event.getAction().equals(Action.RIGHT_CLICK_AIR)||event.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
                        return;
                }
                ItemStack i=event.getPlayer().getInventory().getItemInHand();
                if(i.getType().equals(Material.EMPTY_MAP)){
                        if(i.hasItemMeta()){
                                if(i.getItemMeta().getDisplayName().equals("Empty Cartographer's Map")){
                                        try{
                                                CartographersMap cm=SuperMap.mapItemToCMap.get(new Integer(i.getDurability()));
                                                cm.unroll(event.getPlayer().getInventory().getHeldItemSlot(),event.getPlayer(),plugin,this);
                                        }catch(NullPointerException npe){
                                                CartographersMap cm=new CartographersMap(i);
                                                Integer rawr=Integer.valueOf(i.getDurability());
                                                SuperMap.mapItemToCMap.put(rawr, cm);
                                                cm.unroll(event.getPlayer().getInventory().getHeldItemSlot(),event.getPlayer(),plugin,this);
                                        }
                                }
                        }
                }
            }
            @EventHandler
            public void onMapEnterInventory(PlayerPickupItemEvent event) {              
                int amount= event.getItem().getItemStack().getAmount();
                if(event.getItem().getItemStack().getType().equals(Material.MAP)){
                        SuperMap.regMapCount.put(event.getPlayer().getDisplayName(), SuperMap.regMapCount.get(event.getPlayer().getDisplayName())+1);
                        if(event.getItem().getItemStack().hasItemMeta()){
                                if(event.getItem().getItemStack().getItemMeta().getDisplayName().equals("Cartographer's Map")){
                                        addingCartomap(event.getPlayer(),amount);
                                        try{
                                                Integer rawr=Integer.valueOf(event.getItem().getItemStack().getDurability());
                                                SuperMap.mapItemToCMap.get(rawr).associatePlayer(event.getPlayer());
                                        }catch(NullPointerException npe){
                                                CartographersMap cm=new CartographersMap(event.getPlayer(),event.getItem().getItemStack(),this);
                                                cm.associatePlayer(event.getPlayer());
                                                SuperMap.mapItemToCMap.put(new Integer(event.getItem().getItemStack().getDurability()), cm);
                                        }
                                }
                        }
                }
            }
            @EventHandler
            public void onMapLeaveInventoryQ(PlayerDropItemEvent event) {
                int amount=event.getItemDrop().getItemStack().getAmount();
                if(event.getItemDrop().getItemStack().getType().equals(Material.MAP)){
                        SuperMap.regMapCount.put(event.getPlayer().getDisplayName(), SuperMap.regMapCount.get(event.getPlayer().getDisplayName())+1);
                        if(event.getItemDrop().getItemStack().hasItemMeta()){
                                if(event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals("Cartographer's Map")){
                                        subtractingCartomap(event.getPlayer(),amount);                                  
                                }
                        }
                }
            }
            public void reenableczc(){//this is the full enable function that is called when i need to restart the task
                czc.enableMapCheck();
                if(czcTask==null){
                        czcTask=czc.runTaskTimer(plugin, 0L, 20L*FACTOR);
                }else if(czc.isDead()){
                        czc=czc.clone();
                        czcTask=czc.runTaskTimer(plugin, 0L, 20L*FACTOR);
                }
            }
                public ConstantZoneCheck getCzc() {
                        return czc;
                }
            private boolean isRecipeCorrect(Inventory in) {//if the result is a cartomap, i need to check and see if the recipe has anything other than one cartomap and a one empty cartomap in the ingredients
                boolean cart=false;
                boolean empt=false;
                HashMap<Integer, ? extends ItemStack>hm1=in.all(Material.EMPTY_MAP);
                HashMap<Integer, ? extends ItemStack>hm2=in.all(Material.MAP);
                Iterator<? extends ItemStack> it=hm1.values().iterator();
                while(it.hasNext()){
                        ItemStack item=it.next();
                        if(isEmptyCarto(item)){
                                empt=true;
                                //check to see if there are other empty maps. if so, abandon the recipe
                                Iterator<? extends ItemStack> it2=hm1.values().iterator();
                                while(it2.hasNext()){
                                        ItemStack item2=it2.next();
                                        if(item2.getType().equals(Material.EMPTY_MAP)){
                                                if(!item2.hasItemMeta()){
                                                        return false;
                                                }else if(!item.getItemMeta().getDisplayName().equals("Empty Cartographer's Map")){
                                                        return false;
                                                }
                                        }
                                }
                                break;
                        }
                }
                it=hm2.values().iterator();
                while(it.hasNext()){
                        ItemStack item=it.next();
                        if(isCarto(item)){
                                cart=true;
                                break;
                        }
                }
                return (cart&&empt);
            }
            private void addingCartomap(Player p, int amount){
                czc.addPlayer(p);
                        SuperMap.mapCount.put(p.getDisplayName(), SuperMap.mapCount.get(p.getDisplayName())+amount);
                        //now i have to check if there were no maps, if there were none, this is the first and i need to start keeping track of them
                        if(SuperMap.numCartMaps<1){
                                //this is where i start constantZoneCheck//
                                reenableczc();
                        }
                        SuperMap.numCartMaps+=amount;
                        
            }
            private void subtractingCartomap(Player p, int amount){
                        SuperMap.mapCount.put(p.getDisplayName(), SuperMap.mapCount.get(p.getDisplayName())-amount);
                        if(SuperMap.mapCount.get(p.getDisplayName())<1){
                                czc.removePlayer(p);
                        }
                        SuperMap.numCartMaps-=amount;
                        if(SuperMap.numCartMaps<1){
                                //this is where i disable the constantZoneCheck
                                czc.disableMapCheck();
                        }
            }
            private boolean isCarto(ItemStack i){
                if(i.hasItemMeta()){
                        if(i.getItemMeta().getDisplayName().equals("Cartographer's Map")){
                                return true;
                        }
                }
                return false;
            }
            private boolean isCartoItem(ItemStack i){
                return isCarto(i)&&(i.getType()==Material.MAP);
            }
            private boolean isEmptyCarto(ItemStack i){
                if(i.hasItemMeta()){
                        if(i.getItemMeta().getDisplayName().equals("Empty Cartographer's Map")){
                                return true;
                        }
                }
                return false;
            }
            private int getMapAmount(Player p){
                HashMap<Integer, ? extends ItemStack> hm=p.getInventory().all(Material.MAP);
                Iterator<? extends ItemStack> it =hm.values().iterator();
                int count=0;
                while(it.hasNext()){
                        ItemStack i=it.next();
                        count+=i.getAmount();
                }
                return count;
            }
            private int getCartoMapAmount(Player p){//currently the amount ends up ignoring the amount of each itemstack, i need to update it to count each itemstack for more than just one
                HashMap<Integer, ? extends ItemStack> hm=p.getInventory().all(Material.MAP);
                Iterator<? extends ItemStack> it =hm.values().iterator();
                int count=0;
                while(it.hasNext()){
                        ItemStack i=it.next();
                        if(i.hasItemMeta()){
                                if(i.getItemMeta().getDisplayName().equals("Cartographer's Map")){
                                        count+=i.getAmount();
                                }
                        }
                }
                return count;
            }
                private ShapedRecipe recipe;
            private ConstantZoneCheck czc;
            private BukkitTask czcTask;
            private final long FACTOR=1;//change back to 1 later
}
