package io.github.Stevenator1.NoobStart;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class NoobStart extends JavaPlugin {
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("ns")) {
			return true;
		}
		return false; 
	}
	
	public void onPlayerJoin(PlayerJoinEvent evt) {
	    Player player = evt.getPlayer(); // The player who joined
	    if(player.hasPermission("noobstart.needsrank")){
	    	
	    }
	}
}
