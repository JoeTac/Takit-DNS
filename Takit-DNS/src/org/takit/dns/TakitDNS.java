package org.takit.dns;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class TakitDNS extends JavaPlugin {
	public Logger log = Logger.getLogger("Minecraft");
	
	//private String username = "";
	//private String password = "";
	private String urlCode = "";
	private String host = "";
	private long interval = 0L;
	
	public void onDisable() {
		log.info(String.format(Messages.PLUGIN_DISABLE, getDescription().getName()));
	}

	public void onEnable() {
		initConfig();
		
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
		    	if ( host.equals("freedns.afraid.org") ) {
		    		try {
		    			URL url = new URL("http://freedns.afraid.org/dynamic/update.php?"+urlCode);
		    			InputStream is = url.openStream();
		    			BufferedReader dr = new BufferedReader(new InputStreamReader(is));
		    			
		    			while ( (dr.readLine())!=null)
		    				;
		    			
		    			dr.close();
		    			is.close();
		    		}
		    		catch (Exception e) {
						e.printStackTrace();
					}
		    	}
		    }
		}, (interval*20)*60);
		log.info(String.format(Messages.PLUGIN_ENABLE, getDescription().getName()));
	}
	
	public void initConfig() {
		FileConfiguration config = getConfig();
		
		File configFile = new File(this.getDataFolder(), "dns.yml");
		if ( !configFile.exists() ) {
			config.set("dns.url-code", "url-code");
			config.set("dns.interval", 10);
			saveConfig();
			reloadConfig();
		}
		
		host = config.getString("dns.host", "freedns.afraid.org");
		//username = config.getString("dns.username", "");
		//password = config.getString("dns.password", "");
		urlCode = config.getString("dns.url-code");
		interval = config.getInt("dns.interval");
	}
}
