package com.franga2000.CNRandomTP;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Main extends JavaPlugin implements Listener {
	
	String prefix;
	ArrayList<Biome> biomes = new ArrayList<Biome>();
	Random random = new Random();
	Location loc;
	ConfigurationSection presets;
	
	public void onEnable() {
		saveDefaultConfig();
		saveConfig();
		
		presets = getConfig().getConfigurationSection("presets");
		this.getServer().getPluginManager().registerEvents(this, this);
		
		prefix = getConfig().getString("prefix");
		
		for(String b : getConfig().getStringList("biomes")) {
			biomes.add(Biome.valueOf(b.replace(' ', '_')));
		}
	}
	
	public void onDisable() {
		//saveConfig();
	}
	
	private int randomNum(Integer min, Integer max) {
		return (int) (Math.random() * (max-min)) +min;
	}
	
	private Location getRandomLoc(int min, int max, World world) {
		int x = randomNum(min, max);
		int y = getConfig().getInt("locationY");
		int z = randomNum(min, max);
		return new Location(world, (double) x, (double) y + 1.0, (double) z);
	}
	
	public void randomTP(Player p, String preset) {
		if(!getConfig().getStringList("worlds").contains(p.getWorld().getName())) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + getConfig().getString("messages.WorldDenied")));
			return;
		}
		
		if (presets.getString(preset) == null) {
			System.out.println(presets.getString(preset));
			//Preset not given
			loc = getRandomLoc((getConfig().getInt("worldSize") * -1), getConfig().getInt("worldSize"), p.getWorld());
			
			while(!biomes.contains(loc.getBlock().getBiome())) {
				loc = getRandomLoc((getConfig().getInt("worldSize") * -1), getConfig().getInt("worldSize"), p.getWorld());
			}
	
			p.teleport(loc);
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1200,999), true);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + getConfig().getString("messages.onTeleport")));
		} else {
		//Preset is given
		int locs = presets.getConfigurationSection(preset).getKeys(false).size();
		System.out.println("locs:" + locs);
		int rand = random.nextInt(locs);
		System.out.println(rand + 1);
		p.teleport(deserializeLoc(presets.getString(preset + "." + (rand + 1))));
		}
		return;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
		if (cmd.getLabel().equalsIgnoreCase("randomtp")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + getConfig().getString("messages.NotPlayer")));
				return true;
			}
			Player player = (Player) sender;
			
			randomTP(player, (args.length > 0 ? args[0] : "nope"));
		}
		return true;
	}

	private Location deserializeLoc(String loc) {
		String[] coords = loc.split(",");
		return new Location(this.getServer().getWorld(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), Integer.parseInt(coords[3]));
	}
}
