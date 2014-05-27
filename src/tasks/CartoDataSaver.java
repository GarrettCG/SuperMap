package tasks;

import java.io.IOException;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import plugin.SuperMap;

public class CartoDataSaver extends BukkitRunnable {

	public CartoDataSaver(Plugin plugin){
		this.plugin=plugin;
	}
	@Override
	public void run() {
		try {
			SuperMap.saveCartomapList();
		} catch (IOException e) {
			System.out.println("Failed to save cartomap data from async thread");
			e.printStackTrace();
		}		
	}
	private Plugin plugin;
}
