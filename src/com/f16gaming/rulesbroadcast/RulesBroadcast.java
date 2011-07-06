package com.f16gaming.rulesbroadcast;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class RulesBroadcast extends JavaPlugin {
	RulesBroadcastLogger log = new RulesBroadcastLogger(Logger.getLogger("Minecraft"), "RulesBroadcast");
	RulesBroadcastFileReader fileReader = new RulesBroadcastFileReader();
	private PermissionHandler permissionHandler;
	private boolean hasPermissions = false;
	private String permString = "rulesbroadcast";
	private String permReload = "rulesbroadcast.reload";
	String pluginDir = "plugins/RulesBroadcast/";
	File messageFile = new File(pluginDir + "rules.txt");
	String[] rules;
	
	public void onEnable() {
		File directory = new File(pluginDir);
		if (!directory.exists()) {
			log.info("Plugin directory does not exist, creating...");
			if (directory.mkdirs()) {
				log.info("Created plugin directory!");
			} else {
				log.severe("Failed to create plugin directory, plugin will not function!");
				this.setEnabled(false);
				return;
			}
		}
		if (!messageFile.exists()) {
			log.info("Plugin message file not found, creating empty message file...");
			try {
				messageFile.createNewFile();
				log.info("Created empty message file!");
			} catch(Exception ex) {
				log.severe("Failed to create message file, plugin will not function!");
				ex.printStackTrace();
				this.setEnabled(false);
				return;
			}
		}
		//Load rules from file here
		try {
			log.info("Loading rules from " + messageFile.getPath());
			rules = getRules();
			log.info(rules.length + " rules loaded!");
		} catch(IOException ex) {
			log.severe("Unable to read message file, plugin will not function!");
			ex.printStackTrace();
			this.setEnabled(false);
			return;
		}
		//Check for permissions
		setupPermissions();
		log.info(String.format("v%s by F16Gaming has been enabled!", 0.01));
	}
	
	public void onDisable() {
		log.info(String.format("v%s by F16Gaming has been disabled!", 0.01));
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		//Behold, ugly code!
		//TODO: Make a better way to handle console commands
		boolean retVal = false;
		Player player = null;
		boolean isConsole = sender instanceof ColouredConsoleSender;
		
		if (!isConsole) {
			player = (Player) sender;
			log.info(player.getName() + " issued command " + cmd.getName());
		}
		
		if (commandLabel.equalsIgnoreCase("sendrules")) {
			if (player != null) {
				if (hasPermission(player, permString)) {
					if (args.length == 1) {
						if (!args[0].equalsIgnoreCase("all")) {
							retVal = true;
							Player toPly = this.getServer().getPlayer(args[0]);
							if (sendRules(toPly)) {
								player.sendMessage("\u00a72Sent rules to \u00a76" + toPly.getName());
							} else {
								player.sendMessage("\u00a7cInvalid player name");
							}
						} else {
							retVal = true;
							broadcastRules();
							player.sendMessage("\u00a72Broadcasted rules to server");
						}
					}
				} else {
					player.sendMessage("\u00a7cYou are not allowed to use this command");
					retVal = true;
				}
			} else {
				if (args.length == 1) {
					if (!args[0].equalsIgnoreCase("all")) {
						retVal = true;
						Player toPly = this.getServer().getPlayer(args[0]);
						if (sendRules(toPly)) {
						} else {
							log.info("Invalid player name");
						}
					} else {
						retVal = true;
						broadcastRules();
					}
				}
			}
		}
		
		if (commandLabel.equalsIgnoreCase("reloadrules")) {
			retVal = true;
			if (player != null) {
				if (hasPermission(player, permReload)) {
					if (reloadRules()) {
						player.sendMessage("\u00a72Rules have been reloaded!");
					} else {
						player.sendMessage("\u00a7cFailed to reload rules");
					}
				} else {
					player.sendMessage("\u00a7cYou are not allowed to use this command");
				}
			} else {
				reloadRules();
			}
		}
		return retVal;
	}
	
	private boolean sendRules(Player toPly) {
		boolean retVal = false;
		if (toPly != null) {
			log.info("Sent rules to " + toPly.getName());
			for (String rule : rules) {
				toPly.sendMessage(rule);
			}
			retVal = true;
		}
		return retVal;
	}
	
	private void broadcastRules() {
		for (String rule : rules) {
			this.getServer().broadcastMessage(rule);
		}
		log.info("Broadcasted rules to server");
	}
	
	private boolean reloadRules() {
		boolean retVal = false;
		try {
			rules = getRules();
			log.info(rules.length + " rules have been reloaded!");
			retVal = true;
		} catch(IOException ex) {
			log.severe("Unable to reload rules, plugin might not function!");
			ex.printStackTrace();
		}
		return retVal;
	}
	
	private String[] getRules() throws IOException {
		return fileReader.readAllLines(messageFile.getPath());
	}
	
	private void setupPermissions() {
		Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
		if (permissionHandler == null) {
			if (permissionsPlugin != null) {
				PluginDescriptionFile permPDF = permissionsPlugin.getDescription();
				permissionHandler = ((Permissions) permissionsPlugin).getHandler();
				hasPermissions = true;
				log.info("Permissions v" + permPDF.getVersion() + " found, permissions support enabled!");
			} else {
				log.info("Permissions system not detected, defaulting to isOp");
			}
		}
	}
	
	private boolean hasPermission(Player player, String permission) {
		boolean retVal = false;
		if (hasPermissions) {
			if (permissionHandler.has(player, permission))
				retVal = true;
		}
		else {
			if (player.isOp())
				retVal = true;
		}
		return retVal;
	}
}
