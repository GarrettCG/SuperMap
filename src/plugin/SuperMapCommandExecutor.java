package plugin;

import mapstuff.CartographersMap;


import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

public class SuperMapCommandExecutor implements CommandExecutor {
    private final SuperMap plugin;

    public SuperMapCommandExecutor(SuperMap plugin) {
        this.plugin = plugin;
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
                System.out.println("onCommand");
                if(cmd.getName().equalsIgnoreCase("test")){ // If the player typed /test then do the following...
                        Player player = (Player) sender;
                        if(args!=null){
                                new CartographersMap(player);
                        }
                    player.sendMessage("message sent because /test was triggered");
                        return true;
                }
                if(cmd.getName().equalsIgnoreCase("cot")){
                        Player player = (Player) sender;
                        if(args!=null){
                                System.out.println("player "+sender.getName()+"'s map count is:"+SuperMap.mapCount.get(sender.getName())+" and the numCartoMaps is "+SuperMap.numCartMaps);
                        }
                    player.sendMessage("message sent because /cot was triggered");
                        return true;
                }
                if(cmd.getName().equalsIgnoreCase("hand")){
                        Player player = (Player) sender;
                        if(args!=null){
                                System.out.println(SuperMap.mapItemToCMap);
                                System.out.println(Bukkit.getUpdateFolderFile());
                                System.out.println(Bukkit.getUpdateFolder());
                        }
                    player.sendMessage("message sent because /hand was triggered");
                        return true;
                }
                return false; 
        }
}
