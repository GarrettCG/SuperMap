package plugin;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import listeners.SuperMapListener;
import mapstuff.CartographersMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import tasks.CartoDataSaver;

public final class SuperMap extends JavaPlugin{
        @Override
        public void onEnable(){
                System.out.println("onEnable SuperMap");
        saveDefaultConfig();
        // Create the Listener
        new SuperMapListener(this);
        // set the command executor for my commands
        SuperMapCommandExecutor smce=new SuperMapCommandExecutor(this);
        this.getCommand("test").setExecutor(smce);
        this.getCommand("cot").setExecutor(smce);
        this.getCommand("hand").setExecutor(smce);      
        
        //read in from file first//
        try {
                        loadCartomapList();
                } catch (IOException e) {
                        System.out.println("data file not found... creating new data");
                        mapItemToCMap=new HashMap<Integer,CartographersMap>() ;
                        CartographersMap.emptyId=20000;//starting the mapIds in the 20 thousands in the hopes that they never conflict
                } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                }
        mapCount=new HashMap<String,Integer>();
        regMapCount=new HashMap<String,Integer>();   
        //setting up an annoying arraylist of arraylist that acts as a coordinate translator
        directionToIndex=new ArrayList<ArrayList<Integer>>();
        for(int i=0;i<5;i++){
                directionToIndex.add(new ArrayList<Integer>());
        }
        directionToIndex.get(0).add(0);
        directionToIndex.get(0).add(0);
        directionToIndex.get(0).add(1);
        directionToIndex.get(0).add(2);
        directionToIndex.get(0).add(2);
        
        directionToIndex.get(1).add(0);
        directionToIndex.get(1).add(0);
        directionToIndex.get(1).add(1);
        directionToIndex.get(1).add(2);
        directionToIndex.get(1).add(2);
        
        directionToIndex.get(2).add(3);
        directionToIndex.get(2).add(3);
        directionToIndex.get(2).add(4);
        directionToIndex.get(2).add(5);
        directionToIndex.get(2).add(5);
        
        directionToIndex.get(3).add(6);
        directionToIndex.get(3).add(6);
        directionToIndex.get(3).add(7);
        directionToIndex.get(3).add(8);
        directionToIndex.get(3).add(8);
        
        directionToIndex.get(3).add(6);
        directionToIndex.get(3).add(6);
        directionToIndex.get(3).add(7);
        directionToIndex.get(3).add(8);
        directionToIndex.get(3).add(8);
        

        indexToDirectionX=new ArrayList<Integer>();
        indexToDirectionZ=new ArrayList<Integer>();
        
        indexToDirectionX.add(-2);
        indexToDirectionX.add(-1);
        indexToDirectionX.add(0);
        indexToDirectionX.add(1);
        indexToDirectionX.add(2);
        indexToDirectionX.add(-2);
        indexToDirectionX.add(-1);
        indexToDirectionX.add(0);
        indexToDirectionX.add(1);
        indexToDirectionX.add(2);
        indexToDirectionX.add(-2);
        indexToDirectionX.add(-1);
        indexToDirectionX.add(0);
        indexToDirectionX.add(1);
        indexToDirectionX.add(2);
        indexToDirectionX.add(-2);
        indexToDirectionX.add(-1);
        indexToDirectionX.add(0);
        indexToDirectionX.add(1);
        indexToDirectionX.add(2);
        indexToDirectionX.add(-2);
        indexToDirectionX.add(-1);
        indexToDirectionX.add(0);
        indexToDirectionX.add(1);
        indexToDirectionX.add(2);
        
        indexToDirectionZ.add(2);
        indexToDirectionZ.add(2);
        indexToDirectionZ.add(2);
        indexToDirectionZ.add(2);
        indexToDirectionZ.add(2);
        indexToDirectionZ.add(1);
        indexToDirectionZ.add(1);
        indexToDirectionZ.add(1);
        indexToDirectionZ.add(1);
        indexToDirectionZ.add(1);
        indexToDirectionZ.add(0);
        indexToDirectionZ.add(0);
        indexToDirectionZ.add(0);
        indexToDirectionZ.add(0);
        indexToDirectionZ.add(0);
        indexToDirectionZ.add(-1);
        indexToDirectionZ.add(-1);
        indexToDirectionZ.add(-1);
        indexToDirectionZ.add(-1);
        indexToDirectionZ.add(-1);
        indexToDirectionZ.add(-2);
        indexToDirectionZ.add(-2);
        indexToDirectionZ.add(-2);
        indexToDirectionZ.add(-2);
        indexToDirectionZ.add(-2);
        numCartMaps=0;     
        }
        public void onDisable(){
                getLogger().info("onDisable has been invoked for SuperMap!");
                saveTask.cancel();
                //write to file first//
                try {
                        saveCartomapList();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                mapItemToCMap=null;
                directionToIndex=null;
        indexToDirectionX=null;
        indexToDirectionZ=null;
        mapCount=null;
        regMapCount=null;
        Bukkit.getServer().clearRecipes();
        saveTask=new CartoDataSaver(this).runTaskTimerAsynchronously(this, SAVEINTERVAL, SAVEINTERVAL);
        }
        public static int translateCoordinates(int x, int z){
                return (directionToIndex.get(z+2).get(x+2)).intValue();
        }
        public static void saveCartomapList() throws IOException{
                FileOutputStream fos = new FileOutputStream("cartomap_data.dat");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(mapItemToCMap);
                System.out.println("saving empty id:"+CartographersMap.emptyId);
                oos.writeShort(CartographersMap.emptyId);
                oos.close();
        }
        public static void loadCartomapList() throws IOException, ClassNotFoundException{
                FileInputStream fis = new FileInputStream("cartomap_data.dat");
                ObjectInputStream ois = new ObjectInputStream(fis);
                mapItemToCMap = (HashMap<Integer,CartographersMap>) ois.readObject();
                CartographersMap.emptyId=ois.readShort();
                System.out.println("loaded empty id:"+CartographersMap.emptyId);
                ois.close();
        }
        //these are just hardcoded transformation values
        public static ArrayList<Integer> indexToDirectionX;
        public static ArrayList<Integer> indexToDirectionZ;
        public static ArrayList<ArrayList<Integer>> directionToIndex;
        
        //some map tracking hashmaps
        public static HashMap <Integer,CartographersMap> mapItemToCMap;
        public static HashMap <String,Integer> mapCount;
        public static HashMap <String,Integer> regMapCount;
        //this next value probably doesn't need to be persistent since it is kept track of whenever people join or leave
        public static int numCartMaps;
        //save mapdata task
        public BukkitTask saveTask;
        private final long SAVEINTERVAL=20*60*60;//once every hour 
}
