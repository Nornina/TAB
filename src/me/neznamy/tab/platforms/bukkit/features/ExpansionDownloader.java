package me.neznamy.tab.platforms.bukkit.features;

import java.io.File;
import java.util.ConcurrentModificationException;

import org.bukkit.Bukkit;

import me.clip.placeholderapi.PlaceholderAPI;
import me.neznamy.tab.platforms.bukkit.Main;
import me.neznamy.tab.shared.Shared;

public class ExpansionDownloader{

	public ExpansionDownloader() {
		Main instance = Main.instance;
		Bukkit.getScheduler().runTaskLater(instance, new Runnable() {

			@Override
			public void run() {
				Shared.featureCpu.runTask("Downloading PlaceholderAPI Expansions", new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(5000);
							Shared.debug("All used expansions: " + Main.usedExpansions);
							Shared.debug("Installed expansions: " + PlaceholderAPI.getRegisteredIdentifiers());
							Main.usedExpansions.removeAll(PlaceholderAPI.getRegisteredIdentifiers());
							Shared.debug("Expansions to install: " + Main.usedExpansions);
							if (!Main.usedExpansions.isEmpty()) {
								File expansionsFolder = new File(Bukkit.getPluginManager().getPlugin("PlaceholderAPI").getDataFolder(), "expansions");
								int oldExpansionDownloadedCount = expansionsFolder.listFiles().length;
								for (String expansion : Main.usedExpansions) {
									instance.sendConsoleMessage("&d[TAB] Expansion &e" + expansion + "&d is used but not installed. Installing!");
									runSyncCommand("papi ecloud download " + expansion);
									Thread.sleep(5000);
								}
								if (expansionsFolder.listFiles().length > oldExpansionDownloadedCount) {
									instance.sendConsoleMessage("&d[TAB] Reloading PlaceholderAPI for the changes to take effect");
									runSyncCommand("papi reload");
								}
							}
						} catch (InterruptedException | ConcurrentModificationException e) {
						} catch (Throwable e) {
							Shared.errorManager.printError("Failed to download PlaceholderAPI expansions. PlaceholderAPI version: " + Bukkit.getPluginManager().getPlugin("PlaceholderAPI").getDescription().getVersion(), e);
						}
					}
				});
			}
			
		}, 1);
	}
	public void runSyncCommand(String command) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {

			@Override
			public void run() {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
			}
		});
	}
}