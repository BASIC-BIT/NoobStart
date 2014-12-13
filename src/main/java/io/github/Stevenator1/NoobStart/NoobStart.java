package io.github.Stevenator1.NoobStart;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class NoobStart extends JavaPlugin implements Listener{
	FileConfiguration config;
	Location spawnLoc;
	List<String> kits;
	String baseKit;
	String rank;
	
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		loadConfig();
	}
	
	private void loadConfig(){
		this.saveDefaultConfig();
		config = this.getConfig();
		spawnLoc = new Location(
				Bukkit.getWorld(config.getString("noobspawn.world")),
				config.getDouble("noobspawn.x"),
				config.getDouble("noobspawn.y"),
				config.getDouble("noobspawn.z"),
				(float)config.getDouble("noobspawn.yaw"),
				(float)config.getDouble("noobspawn.pitch"));
		baseKit = config.getString("basekit");
		kits = config.getStringList("noobkits");
		rank = config.getString("rank");
	}
	
	public void reloadConfigAndGetValues(){
		this.reloadConfig();
		loadConfig();
	}
	
	public void saveConfigAndGetValues(){
		this.saveConfig();
		reloadConfigAndGetValues();
	}
	@Override
	public void onDisable() {
		
	}
	
	public void setMetadata(Metadatable object, String key, Object value, Plugin plugin) {
		object.setMetadata(key, new FixedMetadataValue(plugin,value));
	}
		 
	public Object getMetadata(Metadatable object, String key, Plugin plugin) {
		List<MetadataValue> values = object.getMetadata(key);  
		for (MetadataValue value : values) {
			// Plugins are singleton objects, so using == is safe here
			if (value.getOwningPlugin() == plugin) {
				return value.value();
			}
		}
		return null;
	}
	
	private void runServerCmd(String input){
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), input);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("ns")) {
			if(sender.hasPermission("noobstart.admin")){
				if(args[0].equals("setspawn") && (sender instanceof Player)){
					Player p = (Player)sender;
					Location pos = p.getLocation();
					config.set("noobspawn.x", pos.getX());
					config.set("noobspawn.y", pos.getY());
					config.set("noobspawn.z", pos.getZ());
					config.set("noobspawn.pitch", pos.getPitch());
					config.set("noobspawn.yaw", pos.getYaw());
					config.set("noobspawn.world", p.getWorld().getName());
					saveConfigAndGetValues();
					sender.sendMessage("Noob spawn set!");
					return true;
				}
				if(args[0].equals("reload")){
					reloadConfigAndGetValues();
					sender.sendMessage("Noob Start Config Reloaded!");
					return true;
				}
			}
			
			if(sender.hasPermission("noobstart.deny")){
				sender.sendMessage("Sorry, but you've already gone through noob spawn!");
				return true;
			}
			
			if(args[0].equals("accept")){
				if(!sender.hasPermission("noobstart.needsrank")){
					sender.sendMessage("Sorry, but you've already gone through noob spawn!");
					return true;
				}else if(getMetadata((Player)sender, "nsaccept", this).equals(true)){
					sender.sendMessage("You have already accepted the server rules.  To continue with the process, please type '/ns kit *kitname*',"
							+ " where *kitname* is the name of one of the kits on the computer screens.");
				}else{
					sender.sendMessage("Thank you for accepting our server rules!  Now, to finish the process, select your kit!  "
							+ "You can do so by typing '/ns kit *kitname*', where *kitname* is the name of one of the kits on the computer screens.");
					setMetadata((Player)sender, "nsaccept", true, this);
				}
				return true;
			}
			
			if(args[0].equals("kit")){
				if(!sender.hasPermission("noobstart.needsrank")){
					sender.sendMessage("Sorry, but you've already gone through noob spawn!");
					return true;
				} 
				if(getMetadata((Player)sender, "nsaccept", this).equals(true)){
					String inputKit = args[1];
					if(kits.contains(inputKit.toLowerCase())){
						sender.sendMessage("Thank you for selecting your kit!  Again, welcome to Hyperion FTB Servers, and we wish you a fantastic experience!");
						runServerCmd("kit "+inputKit+" "+sender.getName());
						runServerCmd("kit "+baseKit+" "+sender.getName());
						runServerCmd("pex user "+sender.getName()+" group set "+rank);
						runServerCmd("spawn "+sender.getName());
					}else{
						sender.sendMessage("Not a valid kit!  Please choose one of the starting kits listed.");
					}
					return true;
				}else{
	    	    	sender.sendMessage("Sorry, but you need to read and accept the rules before you can do that!  "
	    	    			+ "Please familiarize yourself with the rules on our website:\n \n "
	    	    			+ "http://www.hyperion-mc.com/forum/m/15247263/viewthread/17641580-rules/page/1 \n \n "
	    	    			+ "Once you have read, and accept the rules of our servers, please type '/ns accept' to continue.");
	    	    	return true;
				}
			}
		}
		return false; 
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent evt) {
	    final Player player = evt.getPlayer(); // The player who joined
	    setMetadata(player,"nsaccept", false, this);
	    if(player.hasPermission("noobstart.needsrank")){
	    	player.teleport(spawnLoc);
	    	this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
	    	    public void run(){
	    	    	player.sendMessage("\n\nWelcome to Hyperion FTB Servers!  This our new integrated noob spawn plugin, made by Stevenator1."
	    	    			+ "  To begin, please familiarize yourself with the rules on our website:\n \n"
	    	    			+ "http://www.hyperion-mc.com/forum/m/15247263/viewthread/17641580-rules/page/1 \n \n"
	    	    			+ "Once you have read, and accept the rules of our servers, please type '/ns accept' to continue.");
	    	    }
	    	},50);
	    }
	}
}
